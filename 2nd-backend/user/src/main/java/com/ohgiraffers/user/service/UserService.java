package com.ohgiraffers.user.service;

import com.ohgiraffers.secondbackend.user.dto.request.PasswordUpdateDTO;
import com.ohgiraffers.secondbackend.user.dto.request.ProfileUpdateDTO;
import com.ohgiraffers.secondbackend.user.dto.response.UserResponseDTO;
import com.ohgiraffers.secondbackend.user.entity.User;
import com.ohgiraffers.secondbackend.user.entity.UserRole;
import com.ohgiraffers.secondbackend.user.repository.UserRepository;
import com.ohgiraffers.secondbackend.user.util.JWTUtil;
import jakarta.transaction.Transactional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserService  implements UserDetailsService{

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    private UserResponseDTO userResponseDTO(User user){
        return UserResponseDTO.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .build();
    }

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JWTUtil jwtUtil,
                       RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));
    }

   //회원가입
    @Transactional
    public User signup(String username, String password, String nickname){
        if(userRepository.findByUsername(username).isPresent()){
            throw new IllegalArgumentException("이미 존재함");
        }

        User user= User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .role(UserRole.USER)
                .build();

        return userRepository.save(user);
    }

    //로그인
    public String[] login(String username,String rawPassword){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("잘못된 아이디"));


        if(!passwordEncoder.matches(rawPassword,user.getPassword())){
            throw new IllegalArgumentException("잘못된 비번");
        }

        String role=user.getRole().name();
        String accessToken=jwtUtil.createAccessToken(username,role);
        String refreshToken=jwtUtil.createRefreshToken(username,role);

        String refreshKey="refresh:"+username;
        //28일간 저장
        redisTemplate.opsForValue().set(refreshKey,refreshToken,28, TimeUnit.DAYS);

        return new String[]{accessToken,refreshToken};
    }

    /*로그아웃*/
    public void logout(String accessToken){
        String username=jwtUtil.getUsername(accessToken);
        String refreshKey="refresh:"+username;
        redisTemplate.delete(refreshKey);

        if(!jwtUtil.isTokenExpired(accessToken)){
            String blacklistKey= "blacklist:"+accessToken;
            long expriation = jwtUtil.getExpriation(accessToken)-System.currentTimeMillis();

            if(expriation>0){
                redisTemplate.opsForValue().set(blacklistKey,"logout",expriation, TimeUnit.MILLISECONDS);
            }else{
                redisTemplate.delete(blacklistKey);
            }
        }
    }

   /*닉네임 변경*/
    @Transactional
    public UserResponseDTO updateNickname(String accessToken, ProfileUpdateDTO profileUpdateDTO){
        String username= jwtUtil.getUsername(accessToken);
        User user=userRepository
                .findByUsername(username).orElseThrow(()-> new IllegalArgumentException("잘못된 접근"));

        user.setNickname(profileUpdateDTO.getNickname());
        userRepository.save(user);

        return userResponseDTO(user);
    }

    /*비밀번호 변경*/
    //기존 비밀번호 검증이 필요하지 않을 거 같아서 빼긴했는데...
    public UserResponseDTO updatePassword(String accessToken, PasswordUpdateDTO passwordUpdateDTO){
        String username= jwtUtil.getUsername(accessToken);
        User user=userRepository
                .findByUsername(username).orElseThrow(()-> new IllegalArgumentException("잘못된 접근"));

        user.setPassword(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
        userRepository.save(user);

        return userResponseDTO(user);
    }


}
