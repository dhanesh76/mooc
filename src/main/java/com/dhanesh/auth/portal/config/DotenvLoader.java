// package com.dhanesh.auth.portal.config;

// import org.springframework.stereotype.Component;

// import io.github.cdimascio.dotenv.Dotenv;
// import jakarta.annotation.PostConstruct;

// @Component
// public class DotenvLoader {
    
//     @PostConstruct
//     public void loadenv(){
//         Dotenv dotenv = Dotenv
//             .configure()
//             .ignoreIfMalformed()
//             .ignoreIfMissing()
//             .load();
        
//         dotenv.entries().forEach(
//             entry -> System.setProperty(entry.getKey(), entry.getValue())
//         );
//     }
// }