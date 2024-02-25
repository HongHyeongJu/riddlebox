package com.labmate.riddlebox.service;

import com.labmate.riddlebox.entity.*;
import com.labmate.riddlebox.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final JPAQueryFactory queryFactory;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(EntityManager em, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.queryFactory = new JPAQueryFactory(em);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean isValidNickname(String nickname) {
        QRBUser user = QRBUser.rBUser;
        long count = queryFactory.selectFrom(user)
                                .where(user.nickname.eq(nickname))
                                .fetchCount();
        return count == 0; // 닉네임을 가진 사용자가 없으면 true, 있으면 false 반환
    }

    @PreAuthorize("#loginEmail == authentication.principal.username")
    public void updateMemberInfo(String loginEmail, String newPassword, String newNickname) {
        RBUser user = userRepository.findByLoginEmail(loginEmail)
                                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + loginEmail));

        if (!isValidNickname(newNickname)) {
            throw new IllegalArgumentException("중복 닉네임");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setNickname(newNickname);
        userRepository.save(user);
    }





}


//나중에 수정일 검색에 사용할 코드
/*
private BooleanExpression officialUpdateDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
    if (startDate == null && endDate == null) {
        return null;
    }
    if (startDate != null && endDate == null) {
        return QGame.game.officialUpdateDate.after(startDate).or(QGame.game.officialUpdateDate.eq(startDate));
    }
    if (startDate == null) {
    return QGame.game.officialUpdateDate.before(endDate).or(QGame.game.officialUpdateDate.eq(endDate));
}
return QGame.game.officialUpdateDate.between(startDate, endDate);*/
