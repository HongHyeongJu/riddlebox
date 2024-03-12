package com.labmate.riddlebox.service;

import com.labmate.riddlebox.dto.SocialProfileDto;
import com.labmate.riddlebox.entity.*;
import com.labmate.riddlebox.enumpackage.UserStatus;
import com.labmate.riddlebox.repository.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.labmate.riddlebox.entity.QRBUser.rBUser;

@Service
public class UserServiceImpl implements UserService {

    private final JPAQueryFactory queryFactory;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ForbiddenWordRepository forbiddenWordRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final SocialProfileRepository socialProfileRepository;




    @Autowired
    public UserServiceImpl(EntityManager em, UserRepository userRepository,
                           PasswordEncoder passwordEncoder, ForbiddenWordRepository forbiddenWordRepository,
                           RoleRepository roleRepository,
                           UserRoleRepository userRoleRepository, SocialProfileRepository socialProfileRepository) {
        this.queryFactory = new JPAQueryFactory(em);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.forbiddenWordRepository = forbiddenWordRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.socialProfileRepository = socialProfileRepository;
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

    // 사용자 이메일로 사용자 정보 조회
    public RBUser findUserByEmail(String email) {
        RBUser user = queryFactory
                            .selectFrom(rBUser)
                            .where(rBUser.loginEmail.eq(email))
                            .fetchOne();
        return user;
    }

    // 사용자의 권한 목록 조회
    public Set<String> findUserRolesByEmail(String email) {
        RBUser user = findUserByEmail(email);
        if (user != null) {
            return user.getRoles().stream()
                       .map(role -> role.getName()) // RBRole 엔티티에 getName() 메서드가 있다고 가정
                       .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }


 @Override
 @Transactional
    public RBUser createAndSaveRBUser(String loginEmail, String name, String nickname, String password, SocialProfileDto socialProfileDto) {
        // RBUser 객체 생성
        RBUser newUser = RBUser.builder()
                .loginEmail(loginEmail)
                .name(name)
                .nickname(nickname)
                .password(password) // 실제 사용 시 비밀번호는 암호화 과정을 거쳐야 함
                .passwordSetDate(LocalDate.now())
                .regDate(LocalDateTime.now())
                .lastLoginDate(LocalDateTime.now())
                .status(UserStatus.ACTIVE)
                .build();

        // RBUser 저장
        newUser = userRepository.save(newUser);

        // PLAYER 역할 할당
        RBRole playerRole = roleRepository.findByName("PLAYER").orElseThrow(() -> new RuntimeException("Role not found"));
        UserRole userRole = new UserRole(newUser, playerRole, LocalDateTime.now(), "RiddleBox", true, "회원가입");
        userRoleRepository.save(userRole);

        // 소셜 프로필 저장 (예시 데이터, 실제로는 카카오 등 소셜 로그인 데이터를 사용)
        SocialProfile socialProfile = new SocialProfile(newUser, socialProfileDto.getProvider(),
                                                            socialProfileDto.getProviderId(), socialProfileDto.getProfilePictureURL(),
                                                            socialProfileDto.getRefreshToken());
        socialProfileRepository.save(socialProfile);

        return newUser;
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
