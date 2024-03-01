package com.labmate.riddlebox.service;

import com.labmate.riddlebox.entity.*;
import com.labmate.riddlebox.repository.ForbiddenWordRepository;
import com.labmate.riddlebox.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.labmate.riddlebox.entity.QRBUser.rBUser;

@Service
public class UserServiceImpl implements UserService {

    private final JPAQueryFactory queryFactory;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ForbiddenWordRepository forbiddenWordRepository;

    @Autowired
    public UserServiceImpl(EntityManager em, UserRepository userRepository, PasswordEncoder passwordEncoder, ForbiddenWordRepository forbiddenWordRepository) {
        this.queryFactory = new JPAQueryFactory(em);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.forbiddenWordRepository = forbiddenWordRepository;
    }

    @Override
    public boolean isValidNickname(String nickname) {
        QRBUser user = rBUser;
        long count = queryFactory.selectFrom(user)
                                .where(user.nickname.eq(nickname))
                                .fetchCount();
        // 사용 금지 단어 여부를 확인하는 로직 수정
        boolean isForbidden = forbiddenWordRepository.existsByWord(nickname); // existsByWord 메서드 가정

        // 닉네임이 사용 중이지 않고, 사용 금지 단어도 아닐 경우에만 true 반환
        return count == 0 && !isForbidden;
    }

    @Override
    public boolean checkDuplicateEmail(String email) {
        Integer fetchOne = queryFactory.selectOne() // 결과가 있으면 1을 반환하도록 설정
                                       .from(rBUser) // Q클래스를 사용하여 from 절을 구성
                                       .where(rBUser.loginEmail.eq(email)) // 이메일이 일치하는 조건
                                       .fetchFirst(); // 첫 번째 결과만 가져옴, 결과가 없으면 null 반환

        return fetchOne != null; // 결과가 있으면 true, 없으면 false 반환
    }


    //유저 정보 수정
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
