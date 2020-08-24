package xyz.yuanjin.project;

import xyz.yuanjin.project.util.JwtPayload;
import xyz.yuanjin.project.util.JwtUtil;

import java.text.ParseException;

public class JwtTest {
    public static void main(String[] args) throws ParseException {
        String compactJwts = JwtUtil.createToken();
        System.out.println(compactJwts);

        JwtPayload payload = JwtUtil.authToken(compactJwts);

        System.out.println(payload);
    }
}
