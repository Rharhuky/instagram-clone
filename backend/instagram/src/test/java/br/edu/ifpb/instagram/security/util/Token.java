package br.edu.ifpb.instagram.security.util;

public class Token {

    public static String validToken() {
        return "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJlMThjYWJhYi1kNjcyLTQ2ZjctYjk2Mi04NzQ5N2EyMDc3N2EiLCJuYW1lIjoiUmhhcmh1YW5kcmV3IiwiYWRtaW4iOnRydWUsImlhdCI6MTc0MTU2NzA1MH0.F6Wa5yAqVenDeITbVXRnFwRAiK0ZjmWTODTI1GaSZKbhhX7FI1efy2q15BykS4pOuunKmbX48zAD8jER7L6_vQ";
    }

    public static String invalidToken() {
        return "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IlJoYXJodWFuZHJlIiwiYWRtaW4iOnRydWUsImlhdCI6MTc0MTU2NzA1MH0.IobWnL8FGIwsLFHia-MEjgyDb7a-DlZ4Swu6rsGRRwHujS8TycNhOR3fObh5Lc8wm8OINsFoSSrJm2ID10MCag";
    }

    public static String expiredToken(){
        return "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IiIsImFkbWluIjp0cnVlLCJpYXQiOjE3NDE1Njc5MTMsImV4cCI6MTc0MTQ4MTU2OX0.TJZU2Pam_feBzBnYxtlpW53qDQC_sJWXMPeJf4EEFI_pxiBnvgpmTUcXLOR4psdl09M2mVDeKAMs6gMR0cshiQ";
    }

}
