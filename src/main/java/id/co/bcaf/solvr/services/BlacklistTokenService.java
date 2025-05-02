package id.co.bcaf.solvr.services;

import id.co.bcaf.solvr.model.account.BlacklistToken;
import id.co.bcaf.solvr.repository.BlacklistTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BlacklistTokenService {
    @Autowired
    private BlacklistTokenRepository blacklistTokenRepository;

    public void blacklistToken(String token) {
        BlacklistToken blacklistToken = new BlacklistToken();
        blacklistToken.setToken(token);
        blacklistToken.setBlacklistDate(LocalDateTime.now());
        blacklistTokenRepository.save(blacklistToken);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistTokenRepository.existsByToken(token);
    }
}
