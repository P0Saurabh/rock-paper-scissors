package com.mcnz.rps;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Minimal servlet to make the WAR usable on Tomcat.
 * URL: /app/play?choice=ROCK|PAPER|SCISSORS
 */
@WebServlet(name = "PlayServlet", urlPatterns = {"/play"})
public class PlayServlet extends HttpServlet {

    private enum Move { ROCK, PAPER, SCISSORS }

    private static final Random RNG = new Random();

    private Move randomMove() {
        Move[] m = Move.values();
        return m[RNG.nextInt(m.length)];
    }

    private String result(Move user, Move cpu) {
        if (user == cpu) return "DRAW";
        switch (user) {
            case ROCK:     return (cpu == Move.SCISSORS) ? "WIN" : "LOSE";
            case PAPER:    return (cpu == Move.ROCK)     ? "WIN" : "LOSE";
            case SCISSORS: return (cpu == Move.PAPER)    ? "WIN" : "LOSE";
            default:       return "DRAW";
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String choice = req.getParameter("choice");
        resp.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = resp.getWriter()) {
            if (choice == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("<h3>Missing query param: ?choice=ROCK|PAPER|SCISSORS</h3>");
                return;
            }

            Move user = Move.valueOf(choice.toUpperCase());
            Move cpu = randomMove();
            String verdict = result(user, cpu);

            out.println("<!doctype html><html><head><title>RPS Result</title></head><body>");
            out.printf("<h2>You: %s</h2>%n", user);
            out.printf("<h2>CPU: %s</h2>%n", cpu);
            out.printf("<h1>Result: %s</h1>%n", verdict);
            out.println("<p><a href=\"/app/\">Play again</a></p>");
            out.println("</body></html>");
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("<h3>Invalid choice. Use ROCK, PAPER, or SCISSORS.</h3>");
        }
    }
}
