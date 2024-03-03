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


import java.util.List;
import java.util.stream.Collectors;

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
        // 사용 중인 닉네임인지 확인
        long count = queryFactory.selectFrom(rBUser)
                                .where(rBUser.nickname.eq(nickname))
                                .fetchCount();

        // 금지된 단어 목록 가져오기
        List<String> forbiddenWords = findAllWords(); // f모든 금지 단어를 리스트로 반환

        // 닉네임에 금지된 단어가 포함되어 있는지 확인
        boolean containsForbiddenWord = forbiddenWords.stream()
                                                       .anyMatch(nickname::contains);

        // 닉네임이 사용 중이지 않고, 금지된 단어를 포함하고 있지 않을 경우에만 true 반환
        return count == 0 && !containsForbiddenWord;
    }

    /*금지단어 목록 출력*/
    public List<String> findAllWords() {
        return forbiddenWordRepository.findAll()
                                      .stream()
                                      .map(ForbiddenWord::getWord)
                                      .collect(Collectors.toList());
    }


    @Override
    public boolean checkDuplicateEmail(String email) {
        long exists = queryFactory.selectFrom(rBUser) // Q클래스를 사용하여 from 절을 구성
                                  .where(rBUser.loginEmail.eq(email)) // 이메일이 일치하는 조건
                                  .fetchCount(); // 조건에 맞는 결과의 수를 가져옴

        return exists == 0; // 결과가 1개 이상이면 true, 아니면 false 반환
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
