import java.util.Scanner;

public class PasswordValidator {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String password;

        while(true) {
            System.out.print("Enter Password: ");
            password = sc.nextLine();

            String isValid = validatePassword(password);

            if(isValid.equals("VALID")) {
                System.out.println("Password Accepted");
                break;
            }
            else {
                System.out.println(isValid);
                System.out.println("Please try again");
            }
        }

        sc.close();
    }

    private static String validatePassword(String password) {
        boolean hasUppercase = false;
        boolean hasDigit = false;

        StringBuilder errors = new StringBuilder();

        if(password.length() < 8) {
            errors.append("Password should have atleast 8 characters \n");
        }
        
        for(int i=0; i<password.length(); i++) {
            char ch = password.charAt(i);

            if(Character.isUpperCase(ch)) {
                hasUppercase = true;
            }
            if(Character.isDigit(ch)) {
                hasDigit = true;
            }
        }

        if(!hasUppercase) {
            errors.append("Password should have atleast 1 Uppercase letter \n");
        }
        if(!hasDigit) {
            errors.append("Password should have atleast 1 digit \n");
        }

        if(errors.length() == 0)
            return "VALID";
        return errors.toString();
    }
}