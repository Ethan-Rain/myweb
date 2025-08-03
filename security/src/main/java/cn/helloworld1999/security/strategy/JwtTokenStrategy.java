package cn.helloworld1999.security.strategy;

import cn.helloworld1999.security.dto.JwtPayloadDTO;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Component("JWT")
public class JwtTokenStrategy implements TokenStrategy {
    @Autowired
    private Environment env;
    
    // 签名密钥（需要>=256bit，32位字符串）
    private String SECRET;

    @PostConstruct
    public void init() {
        SECRET = env.getProperty("security.token.secret", "defaultSecretThatIsAtLeast32Chars");
    }

    @Override
    public String generateToken(JwtPayloadDTO payload) {
        try {
            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                    .subject(payload.getSub())
                    .issueTime(new Date(payload.getIat() * 1000))
                    .expirationTime(new Date(payload.getExp() * 1000))
                    .claim("username", payload.getUsername())
                    .claim("roles", payload.getRoles())
                    .claim("clientId", payload.getClientId())
                    .claim("tenantId", payload.getTenantId())
                    .claim("securityLevel", payload.getSecurityLevel())
                    .claim("tokenType", payload.getTokenType() != null ? payload.getTokenType().name() : null);

            JWTClaimsSet claimsSet = claimsBuilder.build();

            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);

            JWSSigner signer = new MACSigner(SECRET);
            signedJWT.sign(signer);

            return signedJWT.serialize();

        } catch (Exception e) {
            throw new RuntimeException("生成JWT失败", e);
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(SECRET);
            return signedJWT.verify(verifier) && !isTokenExpired(signedJWT);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public JwtPayloadDTO extractPayload(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            JwtPayloadDTO payload = new JwtPayloadDTO();
            payload.setSub(claims.getSubject());
            payload.setUsername(claims.getStringClaim("username"));
            payload.setRoles((List<String>) claims.getClaim("roles"));
            payload.setClientId(claims.getStringClaim("clientId"));
            payload.setTenantId(claims.getStringClaim("tenantId"));
            payload.setSecurityLevel(claims.getStringClaim("securityLevel"));
            payload.setIat(claims.getIssueTime().getTime() / 1000);
            payload.setExp(claims.getExpirationTime().getTime() / 1000);

            String tokenTypeStr = (String) claims.getClaim("tokenType");
            if (tokenTypeStr != null) {
                payload.setTokenType(JwtPayloadDTO.TokenType.valueOf(tokenTypeStr));
            }

            return payload;

        } catch (ParseException e) {
            throw new RuntimeException("解析JWT失败", e);
        }
    }

    @Override
    public String getType() {
        return "JWT";
    }

    private boolean isTokenExpired(SignedJWT signedJWT) throws ParseException {
        Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();
        return exp == null || exp.before(new Date());
    }
}