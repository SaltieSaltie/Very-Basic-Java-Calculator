import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.swing.*;

public class Calculator extends JFrame {

    static boolean esteOperator(String s)
    {
        return s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/");
    }

    static int prioritate(String op)
    {
        if (op.equals("*") || op.equals("/"))
        {
            return 2;
        }

        if (op.equals("+") || op.equals("-"))
        {
            return 1;
        }

        return 0;
    }

    static List<String> TransformaInRPN(String expresie)
    {
        List<String> iesire = new ArrayList<>();
        Stack<String> stivaOperatori = new Stack<>();

        int i = 0;

        while (i < expresie.length())
        {
            char c = expresie.charAt(i);

            if (c == ' ')
            {
                i++;
                continue;
            }

            if (Character.isDigit(c))
            {
                StringBuilder numar = new StringBuilder();

                while (i < expresie.length() && Character.isDigit(expresie.charAt(i)))
                {
                    numar.append(expresie.charAt(i));
                    i++;
                }

                iesire.add(numar.toString());
                continue;
            }

            if (c == '(')
            {
                stivaOperatori.push("(");
            }

            else if (c == ')')
            {
                while (!stivaOperatori.isEmpty() && !stivaOperatori.peek().equals("("))
                {
                    iesire.add(stivaOperatori.pop());
                }

                stivaOperatori.pop();
            }

            else if (c == '+' || c == '-' || c == '*' || c == '/')
            {
                String opCurent = String.valueOf(c);

                while (!stivaOperatori.isEmpty() && esteOperator(stivaOperatori.peek()) &&
                    prioritate(stivaOperatori.peek()) >= prioritate(opCurent))
                {
                    iesire.add(stivaOperatori.pop());
                }

                stivaOperatori.push(opCurent);
            }

            i++;
        }

        while (!stivaOperatori.isEmpty())
        {
            iesire.add(stivaOperatori.pop());
        }

        return iesire;
    }

    static double CalculeazaRPN(List<String> tokeni) {

        Stack<Double> stivaValori = new Stack<>();

        for (String token : tokeni)
        {
            if (esteOperator(token))
            {
                double b = stivaValori.pop();
                double a = stivaValori.pop();

                double rezultat;

                switch (token) {
                    case "+":
                        rezultat = a + b;
                        break;
                    case "-":
                        rezultat = a - b;
                        break;
                    case "*":
                        rezultat = a * b;
                        break;
                    case "/":
                        if (b == 0)
                        {
                            throw new RuntimeException("Impartire la zero!");
                        }

                        rezultat = a / b;
                        break;
                    default:
                        throw new RuntimeException("Operator necunoscut: " + token);
                }

                stivaValori.push(rezultat);

            }
            else
            {
                stivaValori.push(Double.parseDouble(token));
            }
        }

        return stivaValori.pop();
    }

    JButton digits[] = {
            new JButton(" 0 "),
            new JButton(" 1 "),
            new JButton(" 2 "),
            new JButton(" 3 "),
            new JButton(" 4 "),
            new JButton(" 5 "),
            new JButton(" 6 "),
            new JButton(" 7 "),
            new JButton(" 8 "),
            new JButton(" 9 ")
    };

    JButton operators[] = {
            new JButton(" + "),
            new JButton(" - "),
            new JButton(" * "),
            new JButton(" / "),
            new JButton(" = "),
            new JButton(" C ")
    };

    JButton btnParantezaDeschisa = new JButton("(");
    JButton btnParantezaInchisa = new JButton(")");

    String oper_values[] = {"+", "-", "*", "/", "=", ""};

    String value;
    char operator;

    int TEXTAREA_HEIGHT = 6;
    int TEXTAREA_WIDTH = 10;

    JTextArea area = new JTextArea(TEXTAREA_HEIGHT, TEXTAREA_WIDTH);

    public static void main(String[] args) {
        int CALCULATOR_HEIGHT = 720;
        int CALCULATOR_WIDTH = 1280;

        Calculator calculator = new Calculator();
        calculator.setSize(CALCULATOR_WIDTH, CALCULATOR_HEIGHT);
        calculator.setTitle(" Java-Calc - Forma Poloneza");
        calculator.setResizable(false);
        calculator.setVisible(true);
        calculator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public Calculator() {
        add(new JScrollPane(area), BorderLayout.NORTH);
        JPanel buttonpanel = new JPanel();
        buttonpanel.setLayout(new FlowLayout());

        for (JButton digit : digits)
            buttonpanel.add(digit);

        for (JButton jButton : operators)
            buttonpanel.add(jButton);

        buttonpanel.add(btnParantezaDeschisa);
        buttonpanel.add(btnParantezaInchisa);

        add(buttonpanel, BorderLayout.CENTER);
        area.setForeground(Color.BLACK);
        area.setBackground(Color.WHITE);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);

        for (int i=0;i<digits.length;i++) {
            final int finalI = i;
            digits[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    area.append(Integer.toString(finalI));
                }
            });
        }

        for (int i=0;i<operators.length;i++){
            int finalI = i;
            operators[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (finalI == 5)
                        area.setText("");
                    else
                    if (finalI == 4) {
                        String expresie = area.getText().trim();

                        try {
                            List<String> rpn = TransformaInRPN(expresie);

                            double rezultat = CalculeazaRPN(rpn);

                            if (rezultat == Math.floor(rezultat) && !Double.isInfinite(rezultat))
                            {
                                area.append(" = " + (long) rezultat);
                            }
                            else
                            {
                                area.append(" = " + rezultat);
                            }
                        } catch (Exception e) {
                            area.setText(" !!!Probleme!!! ");
                        }
                    }
                    else {
                        area.append(oper_values[finalI]);
                        operator = oper_values[finalI].charAt(0);
                    }
                }
            });
        }

        btnParantezaDeschisa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                area.append("(");
            }
        });

        btnParantezaInchisa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                area.append(")");
            }
        });

    }
}