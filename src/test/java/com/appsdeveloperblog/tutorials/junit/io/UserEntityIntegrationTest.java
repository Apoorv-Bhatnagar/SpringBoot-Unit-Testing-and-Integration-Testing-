package com.appsdeveloperblog.tutorials.junit.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.util.UUID;

@DataJpaTest
public class UserEntityIntegrationTest {
    @Autowired
    private TestEntityManager testEntityManager;
    UserEntity userEntity;

    @BeforeEach
    void setup()
    {
        userEntity=new UserEntity();
        userEntity.setUserId(UUID.randomUUID().toString());
        userEntity.setFirstName("apoorv");
        userEntity.setLastName("bhatnagar");
        userEntity.setEmail("a@gmail.com");
        userEntity.setEncryptedPassword("12345");

    }
    @Test
    void testUserEntity_whenValidUserDetailsProvided()
    {


        UserEntity storedUserEntity = testEntityManager.persistAndFlush(userEntity);
        Assertions.assertTrue(storedUserEntity.getId() > 0);
        Assertions.assertEquals(userEntity.getUserId(),storedUserEntity.getUserId());
Assertions.assertEquals(userEntity.getFirstName(),storedUserEntity.getFirstName());
    }
    @Test
    void testUserEntity_WhenFirstNameIsTooLong()
    {
         userEntity.setFirstName("11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
    Assertions.assertThrows(PersistenceException.class,()->
    {
        testEntityManager.persistAndFlush(userEntity);
    });
    }
}
