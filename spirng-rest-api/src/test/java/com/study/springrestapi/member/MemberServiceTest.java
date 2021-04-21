package com.study.springrestapi.member;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class MemberServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoderl;

    @Test
    public void findByUserName() {
        //Given
        String username = "sangwoo@gmail.com";
        String password = "sangwoo123";
        Member member = Member.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        this.memberService.saveMember(member);

        //When
        UserDetailsService userDetailsService = (UserDetailsService) memberService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        //Then
        assertThat(this.passwordEncoderl.matches(password,userDetails.getPassword())).isTrue();
    }

    @Test(expected = UsernameNotFoundException.class)
    public void findByUsernameFail() {
        String username = "random@gmail.com";
        memberService.loadUserByUsername(username);
    }

    @Test
    public void findByUsernameFail2() {
        String username = "random@gmail.com";
        try {
            memberService.loadUserByUsername(username);
            fail("supposed to be failed");
        } catch (UsernameNotFoundException e) {
            assertThat(e.getMessage()).containsSequence(username);
        }
    }

    @Test
    public void findByUsernameFail3() {
        //Expected
        String username = "random@gmail.com";
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage(Matchers.containsString(username));

        //When
        memberService.loadUserByUsername(username);
    }
}
