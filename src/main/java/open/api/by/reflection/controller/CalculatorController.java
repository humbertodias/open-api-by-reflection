package open.api.by.reflection.controller;

import javax.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = {"/calc"})
public class CalculatorController extends Controller {

    public int sum(int a, int b){
        return a + b;
    }

    public float sum(float a, float b){
        return a + b;
    }

}
