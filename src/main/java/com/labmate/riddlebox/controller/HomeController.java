package com.labmate.riddlebox.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labmate.riddlebox.dto.GameListDto;
import com.labmate.riddlebox.dto.GameSearchCondition;
import com.labmate.riddlebox.service.GameService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    GameService gameService;

    @Value("${jwt.secret}")
    private String JWT_KEY;


    @GetMapping("/")
    public String home(Model model) {

        System.out.println("===============(home)인증 객체 생성 및 SecurityContextHolder에 저장============= ");


            // 콘솔에 인증된 사용자 정보 출력
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                // 사용자 이름 출력
                String username = auth.getName(); // 또는 principalDetails.getUsername() 사용
                System.out.println("인증된 사용자 이름: " + username);

                // 권한(역할) 목록 출력
                Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
                System.out.println("권한 목록:");
                for (GrantedAuthority authority : authorities) {
                    System.out.println(" - " + authority.getAuthority());
                }
            } else {
                System.out.println("인증된 사용자가 없습니다.");
            }


        return "redirect:/index";
    }



    @GetMapping("/index") //로그인 안한 모든 사용자를 위한 index페이지
    public String Homepage(Model model) {

        //추천게임 목록 받기
        List<GameListDto> gameListDtos = gameService.fetchRecommendedGamesForHomepage();

        System.out.println("===============(index)인증 객체 생성 및 SecurityContextHolder에 저장============= ");


            // 콘솔에 인증된 사용자 정보 출력
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                // 사용자 이름 출력
                String username = auth.getName(); // 또는 principalDetails.getUsername() 사용
                System.out.println("인증된 사용자 이름: " + username);

                // 권한(역할) 목록 출력
                Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
                System.out.println("권한 목록:");
                for (GrantedAuthority authority : authorities) {
                    System.out.println(" - " + authority.getAuthority());
                }
            } else {
                System.out.println("인증된 사용자가 없습니다.");
            }


        //모델에 담기
        model.addAttribute("gameListDtos", gameListDtos);
        model.addAttribute("pageType", "homepage");
        model.addAttribute("title", "RiddleBox");

        return "layout/layout_base";

    }


    /* 게임 목록 조회 */
    @GetMapping("/games")
    public String showGames(Model model) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<GameListDto> games = gameService.searchGameSimple(new GameSearchCondition(), pageable);
        model.addAttribute("games", games);
        return "layout/layout_base";
    }



    /*
    @GetMapping("/home")  //로그인한 사용자의 index페이지
    public String HomepageLoing(Model model){


        //추천게임 Table에서 받아오기 // TODO: 2024-01-16 나중에 데이터가 많아지면 3개월간 조회 많은 순/ 사용자 선호 카테고리+안해본게임


        //이어하기 데이터 1건


        //모델에 담기


        //return index.html

    }*/


}
