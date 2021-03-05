import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {


        // new string transform

        var result = "test string"
                .transform(input -> input + " bar")
                .transform(String::toUpperCase);
        System.out.println(result);

        // teeing

        var employeeList = Arrays.asList(
                new Employee(1, "A", 100),
                new Employee(2, "B", 200),
                new Employee(3, "C", 300),
                new Employee(4, "D", 400));

        var results = employeeList.stream().collect(
                Collectors.teeing(
                        Collectors.maxBy(Comparator.comparing(Employee::getSalary)),
                        Collectors.minBy(Comparator.comparing(Employee::getSalary)),
                        (e1, e2) -> {
                            HashMap<String, Employee> map = new HashMap();
                            map.put("MAX", e1.get());
                            map.put("MIN", e2.get());
                            return map;
                        }
                ));

        System.out.println(results);

        double mean = Stream.of(1, 2, 3, 4, 5)
                .collect(Collectors.teeing(
                        Collectors.summingDouble(i -> i),
                        Collectors.counting(),
                        (sum, count) -> sum / count));

        // new switch case syntax


        var type = TYPE.PLAY;

        var value = switch (type) {
            case PLAY -> "starting the game";
            case DELETE -> {
                var complex = "123" + 123;
                complex = "new value";
                yield complex;
            }
            default -> "default";
        };

        System.out.println(value);

        /// multi line string in java what a time to be alive

        var html = """
                <html>
                    java 14 rocks but escaping still works like this \\ but this is cool  "
                </html>
                """;

        System.out.println(html);


        // pattern matching instance of

        Object employee = new Employee(1, "Bob", 500_000);

        if (employee instanceof Employee emp) {
            System.out.println(emp.getSalary());
        }

        // record

        BankTransaction record = new BankTransaction(LocalDate.now(), BigDecimal.valueOf(123), "transfer");

        System.out.println("Amount: " + record.amount + " description: " + record.description);


        // HTTTP client 9

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://hvg.hu"))
                .GET()
                .build();


        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        //System.out.println(response.body());

        /// private default methods in interfaces

        var p2 = new Person();

        p2.process();

        // new immutables

        var oldList = new ArrayList<String>();

        oldList.add("test");
        oldList.add("123");
        //j 10 real copy,  not by memory addr
        List<String> list2 = List.copyOf(oldList);
        oldList.add("new element");

        System.out.println("Size of oldList: " + oldList.size() + " Size of copy list: " + list2.size());

        // j9
        List<String> list = List.of("test1", "test2");
        try {
            list.add("General Kenobi");
        } catch (UnsupportedOperationException e) {
            System.out.println("Nono! Its immutable!");
        }

        //  unmodifiable

        List<Integer> evenList = List.of(1, 2, 3, 4, 5).stream()
                .filter(i -> i % 2 == 0)
                .collect(Collectors.toUnmodifiableList());

        try {
            evenList.add(4);
        } catch (UnsupportedOperationException e) {
            System.out.println("Nono! Its unmodifiable!");
        }

        // Optinal

        var optinal = Optional.of("String");

        optinal.get();

        optinal.orElseGet(() -> "default value");

        optinal.orElse("deafult value without supplier");

        optinal.orElseThrow();

        // new string methods
        // isBlank, lines, strip, stripLeading, stripTrailing, and repeat

        String test = "test";

        System.out.println("Test variable is blank: " + test.isBlank());

        test = "    TEXT  UUU  III    ";

        System.out.println("Strip: " + test.strip());
        System.out.println("Strip leading: " + test.stripLeading());
        System.out.println("Strip trailing: " + test.stripTrailing());
        System.out.println("Repeat: " + test.repeat(99));
        System.out.println("Indent: " + test.indent(10));


        // nio files

        Path filePath = Files.writeString(Files.createTempFile(Paths.get(""), "demo", ".txt"), "Sample text");
        String fileContent = Files.readString(filePath);
        System.out.println("file contents: " + fileContent);

        Path filePath1 = Files.createTempFile("file1", ".txt");
        Path filePath2 = Files.createTempFile("file2", ".txt");
        Files.writeString(filePath1, "Java12");
        Files.writeString(filePath2, "Java12");

        long mismatch = Files.mismatch(filePath1, filePath2);

        System.out.println("The files are identical so the output is -1L: " + mismatch);

        Files.writeString(filePath1, "plus text 123 123 plus text 123 123");

        mismatch = Files.mismatch(filePath1, filePath2);

        System.out.println("Now the files are not identical: " + mismatch);

        // collection to array

        var sampleList = Arrays.asList("Java", "Kotlin");
        var sampleArray = sampleList.toArray(String[]::new);

        // preicate not

        List<String> list3 = List.of("123", "456", "yes")
                .stream().filter(Predicate.not(item -> item.isBlank()))
                .collect(Collectors.toList());

        System.out.println("Size should be three and it is: " + list3.size());


        // getCompactNumberInstance

        NumberFormat likesShort =
                NumberFormat.getCompactNumberInstance(new Locale("hu", "HU"), NumberFormat.Style.SHORT);
        likesShort.setMaximumFractionDigits(2);
        NumberFormat likesShort2 =
                NumberFormat.getCompactNumberInstance(new Locale("en", "US"), NumberFormat.Style.SHORT);
        likesShort.setMaximumFractionDigits(5);

        System.out.println(likesShort.format(1234.1234));
        System.out.println(likesShort2.format(1234.1234));

        // java 14 NPE
        Person p = new Person();
        p.getName().equals("23");

    }

    public record BankTransaction(LocalDate date, BigDecimal amount, String description) {

    }

    enum TYPE {
        PLAY, STOP, PAUSE, REMOVE, DELETE;
    }

    static class Person implements MainInterface {
        private String name = null;

        public String getName() {
            return name;
        }

    }

    static class Employee {
        private long id;
        private String name;
        private double salary;

        public Employee(long id, String name, double salary) {
            super();
            this.id = id;
            this.name = name;
            this.salary = salary;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getSalary() {
            return salary;
        }

        @Override
        public String toString() {
            return "Employee [id=" + id + ", name=" + name + ", salary=" + salary + "]";
        }
    }

    static class SpecialEmployee extends Employee {

        public SpecialEmployee(long id, String name, double salary) {
            super(id, name, salary);
        }
    }

    interface MainInterface {
        default void process() {
            someOtherHelperMethod();
        }

        private void someOtherHelperMethod() {
            System.out.println("wow private default methods");
        }

    }
}
