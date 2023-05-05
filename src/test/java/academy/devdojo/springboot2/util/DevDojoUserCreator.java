package academy.devdojo.springboot2.util;

import academy.devdojo.springboot2.domain.DevDojoUser;

public class DevDojoUserCreator {

    public static DevDojoUser createDevDojoUserWithRoleUser() {
        return DevDojoUser.builder()
                .name("test")
                .username("test")
                .password("{bcrypt}$2a$10$6Va/Xp4ZlG7IgfKSqdt54eF2bS4hNvd4jkLK9p9/yAyg6bH8cyFCa")
                .authorities("ROLE_USER")
                .build();
    }

    public static DevDojoUser createDevDojoUserWithRoleAdmin() {
        return DevDojoUser.builder()
                .name("admin")
                .username("admin")
                .password("{bcrypt}$2a$10$6Va/Xp4ZlG7IgfKSqdt54eF2bS4hNvd4jkLK9p9/yAyg6bH8cyFCa")
                .authorities("ROLE_USER,ROLE_ADMIN")
                .build();
    }
}
