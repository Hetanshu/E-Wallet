package com.example.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    private final String REDIS_PREFIX_USER="user::";
    private final String CREATE_WALLET_TOPIC="create_wallet";
    public User getUserByUserName(String userName) throws UserNotFoundException {
        Map map=redisTemplate.opsForHash().entries(REDIS_PREFIX_USER+userName);

        if(map==null || map.size()==0){
            // cache miss -> Now Search in DB
            User user=userRepository.findByUserName(userName);

            if(user!=null){
                saveInCache(user);
            }else{
                throw new UserNotFoundException();
            }
            return user;
        }else{
            return objectMapper.convertValue(map,User.class);
        }
    }

    public void createUser(UserRequest userRequest) {
        User user=User.builder()
                .age(userRequest.getAge())
                .name(userRequest.getName())
                .username(userRequest.getUsername())
                .email(userRequest.getEmail()).build();
        userRepository.save(user);
        saveInCache(user);
    }

    private void saveInCache(User user) {
        Map map=objectMapper.convertValue(user, Map.class);
        redisTemplate.opsForHash().putAll(REDIS_PREFIX_USER+user.getUsername(),map);
        redisTemplate.expire(REDIS_PREFIX_USER+user.getUsername(), Duration.ofHours(12));
    }

}
