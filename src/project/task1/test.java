package project.task1;

import java.util.ArrayList;
import java.util.Comparator;

public class test {
    public static void main(String[] args) {
        ArrayList<Employee> employees = new ArrayList<>();
        employees.add(new Employee("jack",21000,new MyDate(2000,11,11)));
        employees.add(new Employee("xl",21000,new MyDate(2000,11,1)));
        employees.add(new Employee("tom",21000,new MyDate(2000,1,11)));

        System.out.println("employees=" + employees);
        System.out.println("-------排序----------");
        employees.sort(new Comparator<Employee>() {
            @Override
            public int compare(Employee emp1, Employee emp2) {
                if (!(emp1 instanceof Employee && emp2 instanceof Employee)) {
                    return 0;
                }
                int i = emp1.getName().compareTo(emp2.getName());
                if (i != 0) {
                    return i;
                }
                return  emp1.getBirthday().compareTo(emp2.getBirthday());

            }
        });
        System.out.println("employee" + employees);
    }
}
