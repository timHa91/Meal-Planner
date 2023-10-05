import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testing.TestedProgram;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MealPlannerTests extends StageTest {

  static final String DB_URL = "jdbc:postgresql:meals_db";
  static final String USER = "postgres";
  static final String PASS = "1111";

  public class Column {
    private String first;
    private String second;

    public Column(String first, String second) {
      this.first = first;
      this.second = second;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Column column = (Column) o;
      return Objects.equals(first, column.first) && Objects.equals(second, column.second);
    }

    @Override
    public int hashCode() {
      return Objects.hash(first, second);
    }
  }

  class dbTable {
    String name;
    List<Column> columnNameType;

    public dbTable(String name, List<Column> columnNameType) {
      this.name = name;
      this.columnNameType = columnNameType;
    }
  }

  void checkTableSchema(List<dbTable> tables) {
    try {
      Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
      DatabaseMetaData meta = connection.getMetaData();
      for (dbTable table : tables) {
        ResultSet tableMeta = meta.getTables(null, null, table.name, null);
        if (!tableMeta.next() || !table.name.equalsIgnoreCase(tableMeta.getString("TABLE_NAME"))) {
          throw new WrongAnswer("The table \"" + table.name + "\" doesn't exist.");
        }
        ResultSet columns = meta.getColumns(null, null, table.name, null);
        List<Column> columnsData = new ArrayList<>();
        while (columns.next()) {
          Column column = new Column(
                  columns.getString("COLUMN_NAME").toLowerCase(),
                  columns.getString("TYPE_NAME").toLowerCase());
          columnsData.add(column);
        }
        for (Column c : table.columnNameType) {
          if (!columnsData.contains(c)) {
            for (Column c2 : columnsData) {
              if (c.first.equals(c2.first)) {
                throw new WrongAnswer("The column \"" + c.first + "\" of the table \"" + table.name + "\" is of the " +
                        "wrong type.");
              }
            }
            throw new WrongAnswer("The column \"" + c.first + "\" of the table \"" + table.name + "\" doesn't exist.");
          }
        }
      }
      connection.close();
    } catch (Exception e) {
      throw new WrongAnswer("An exception was thrown, while trying to check the database schema - " + e.getMessage());
    }
  }

  void checkConnection() {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      throw new WrongAnswer("An exception was thrown, while trying to connect to database. PostgreSQL JDBC Driver is " +
              "not found.");
    }
    Connection connection = null;
    try {
      connection = DriverManager.getConnection(DB_URL, USER, PASS);
    } catch (SQLException e) {
      throw new WrongAnswer("An exception was thrown, while trying to connect to database. Connection Failed");
    }

    if (connection == null) {
      throw new WrongAnswer("Failed to make connection to database");
    }
  }

  @DynamicTest(order = 1)
  public CheckResult normalExe9Test() {
    checkConnection();
    Connection connection = null;
    try {
      connection = DriverManager.getConnection(DB_URL, USER, PASS);
    } catch (Exception e) {
      return CheckResult.wrong("An exception was thrown, while trying to connect to database. Connection Failed");
    }
    try {
      Statement statement = connection.createStatement();
      statement.executeUpdate("DROP TABLE if exists plan");
      statement.executeUpdate("DROP TABLE if exists ingredients");
      statement.executeUpdate("DROP TABLE if exists meals");
    } catch (Exception e) {
      return CheckResult.wrong("An exception was thrown, while trying to drop tables - "+e);
    }

    CheckOutput co = new CheckOutput();
    if (!co.start("What would you like to do (add, show, exit)?"))
      return CheckResult.wrong("Your program should ask the user about the required action: \"(add, show, exit)?\"");
    ArrayList<dbTable> tables = new ArrayList<>(Arrays.asList(
            new dbTable("ingredients", new ArrayList<>(
                    Arrays.asList(
                            new Column("ingredient", "varchar"),
                            new Column("ingredient_id", "int4"),
                            new Column("meal_id", "int4")
                    )
            )),
            new dbTable("meals", new ArrayList<>(
                    Arrays.asList(
                            new Column("category", "varchar"),
                            new Column("meal", "varchar"),
                            new Column("meal_id", "int4")
                    )
            ))
    ));
    checkTableSchema(tables);

    if (!co.input("exit", "Bye!"))
      return CheckResult.wrong("Your output should contain \"Bye!\"");
    if (!co.programIsFinished())
      return CheckResult.wrong("The application didn't exit.");
    return CheckResult.correct();
  }

  @DynamicTest(order = 2)
  CheckResult normalExe10Test() {
    checkConnection();
    Connection connection = null;
    try {
      connection = DriverManager.getConnection(DB_URL, USER, PASS);
    } catch (Exception e) {
      return CheckResult.wrong("An exception was thrown, while trying to connect to database. Connection Failed");
    }
    try {
      Statement statement = connection.createStatement();
      statement.executeUpdate("DROP TABLE if exists plan");
      statement.executeUpdate("DROP TABLE if exists ingredients");
      statement.executeUpdate("DROP TABLE if exists meals");
    } catch (Exception e) {
      return CheckResult.wrong("An exception was thrown, while trying to drop tables - "+e);
    }


    try {
      CheckOutput co = new CheckOutput();
      if (!co.start("What would you like to do (add, show, exit)?"))
        return CheckResult.wrong("Your program should ask the user about the required action: \"(add, show, exit)?\"");

      if (!co.input("add", "Which meal do you want to add (breakfast, lunch, dinner)?"))
        return CheckResult.wrong("Your program should ask the user about meal category: \"(breakfast, lunch, dinner)?\"");

      if (!co.input("lunch", "Input the meal's name:"))
        return CheckResult.wrong("Your output should contain \"Input the meal's name:\"");

      if (!co.input("sushi", "Input the ingredients:"))
        return CheckResult.wrong("Your output should contain \"Input the ingredients:\"");

      if (!co.input("salmon, rice, avocado", "The meal has been added!"))
        return CheckResult.wrong("Your output should contain \"The meal has been added!\"");

      if (!co.inputNext("What would you like to do (add, show, exit)?"))
        return CheckResult.wrong("Your program should ask the user about the required action: \"(add, show, exit)?\"");

      if (!co.input("add", "Which meal do you want to add (breakfast, lunch, dinner)?"))
        return CheckResult.wrong("Your program should ask the user about meal category: \"(breakfast, lunch, dinner)?\"");

      if (!co.input("lunch", "Input the meal's name:"))
        return CheckResult.wrong("Your output should contain \"Input the meal's name:\"");

      if (!co.input("omelette", "Input the ingredients:"))
        return CheckResult.wrong("Your output should contain \"Input the ingredients:\"");

      if (!co.input("eggs, milk, cheese", "The meal has been added!"))
        return CheckResult.wrong("Your output should contain \"The meal has been added!\"");

      if (!co.inputNext("What would you like to do (add, show, exit)?"))
        return CheckResult.wrong("Your program should ask the user about the required action: \"(add, show, exit)?\"");

      if (!co.input("add", "Which meal do you want to add (breakfast, lunch, dinner)?"))
        return CheckResult.wrong("Your program should ask the user about meal category: \"(breakfast, lunch, dinner)?\"");

      if (!co.input("breakfast", "Input the meal's name:"))
        return CheckResult.wrong("Your output should contain \"Input the meal's name:\"");

      if (!co.input("oatmeal", "Input the ingredients:"))
        return CheckResult.wrong("Your output should contain \"Input the ingredients:\"");

      if (!co.input("oats, milk, banana, peanut butter", "The meal has been added!"))
        return CheckResult.wrong("Your output should contain \"The meal has been added!\"");

      if (!co.inputNext("What would you like to do (add, show, exit)?"))
        return CheckResult.wrong("Your program should ask the user about the required action: \"(add, show, exit)?\"");

      if (!co.input("show", "Which category do you want to print (breakfast, lunch, dinner)?"))
        return CheckResult.wrong("Your program should ask the user about the meal category to print: \"(breakfast, lunch, dinner)?\"");

      if (!co.input("lunch", "Category: lunch", "Name: sushi", "Ingredients:", "salmon", "rice", "avocado",
              "Name: omelette", "Ingredients:", "eggs", "milk", "cheese"))
        return CheckResult.wrong("Wrong \"show\" output for a saved meal.");

      if (!co.inputNext("What would you like to do (add, show, exit)?"))
        return CheckResult.wrong("Your program should ask the user about the required action: \"(add, show, exit)?\"");

      if (!co.input("show", "Which category do you want to print (breakfast, lunch, dinner)?"))
        return CheckResult.wrong("Your program should ask the user about the meal category to print: \"(breakfast, lunch, dinner)?\"");

      if (!co.input("breakfast", "Category: breakfast", "Name: oatmeal", "Ingredients:", "oats",
              "milk", "banana", "peanut butter"))
        return CheckResult.wrong("Wrong \"show\" output for a saved meal.");

      if (!co.inputNext("What would you like to do (add, show, exit)?"))
        return CheckResult.wrong("Your program should ask the user about the required action: \"(add, show, exit)?\"");

      if (!co.input("exit", "Bye!"))
        return CheckResult.wrong("Your output should contain \"Bye!\"");

      if (!co.programIsFinished())
        return CheckResult.wrong("The application didn't exit.");
    } catch (Exception e) {
      return CheckResult.wrong("An exception was thrown while testing - "+e);
    }

    return CheckResult.correct();
  }

  @DynamicTest(order = 3)
  CheckResult normalExe11Test() {
    checkConnection();
    Connection connection = null;
    try {
      connection = DriverManager.getConnection(DB_URL, USER, PASS);
    } catch (Exception e) {
      return CheckResult.wrong("An exception was thrown, while trying to connect to database. Connection Failed");
    }

    try {
      CheckOutput co = new CheckOutput();
      if (!co.start("What would you like to do (add, show, exit)?"))
        return CheckResult.wrong("Your program should ask the user about the required action: \"(add, show, exit)?\"");

      if (!co.input("show", "Which category do you want to print (breakfast, lunch, dinner)?"))
        return CheckResult.wrong("Your program should ask the user about the meal category to print: \"(breakfast, lunch, dinner)?\"");

      if (!co.input("lunch", "Category: lunch", "Name: sushi", "Ingredients:", "salmon", "rice", "avocado",
              "Name: omelette", "Ingredients:", "eggs", "milk", "cheese"))
        return CheckResult.wrong("Wrong \"show\" output for a saved meal.");

      if (!co.inputNext("What would you like to do (add, show, exit)?"))
        return CheckResult.wrong("Your program should ask the user about the required action: \"(add, show, exit)?\"");

      if (!co.input("show", "Which category do you want to print (breakfast, lunch, dinner)?"))
        return CheckResult.wrong("Your program should ask the user about the meal category to print: \"(breakfast, lunch, dinner)?\"");

      if (!co.input("breakfast", "Category: breakfast", "Name: oatmeal", "Ingredients:", "oats",
              "milk", "banana", "peanut butter"))
        return CheckResult.wrong("Wrong \"show\" output for a saved meal.");

      if (!co.inputNext("What would you like to do (add, show, exit)?"))
        return CheckResult.wrong("Your program should ask the user about the required action: \"(add, show, exit)?\"");

      if (!co.input("show", "Which category do you want to print (breakfast, lunch, dinner)?"))
        return CheckResult.wrong("Your program should ask the user about the meal category to print: \"(breakfast, lunch, dinner)?\"");

      if (!co.input("brunch", "Wrong meal category! Choose from: breakfast, lunch, dinner."))
        return CheckResult.wrong("Wrong output after the input of a category that doesn't exist.");

      if (!co.input("dinner", "No meals found."))
        return CheckResult.wrong("Wrong output for a category with no added meals.");

      if (!co.input("add", "Which meal do you want to add (breakfast, lunch, dinner)?"))
        return CheckResult.wrong("Your program should ask the user about meal category: \"(breakfast, lunch, dinner)?\"");

      if (!co.input("dinner", "Input the meal's name:"))
        return CheckResult.wrong("Your output should contain \"Input the meal's name:\"");

      if (!co.input("soup", "Input the ingredients:"))
        return CheckResult.wrong("Your output should contain \"Input the ingredients:\"");

      if (!co.input("potato, rice, mushrooms", "The meal has been added!"))
        return CheckResult.wrong("Your output should contain \"The meal has been added!\"");

      if (!co.inputNext("What would you like to do (add, show, exit)?"))
        return CheckResult.wrong("Your program should ask the user about the required action: \"(add, show, exit)?\"");

      if (!co.input("show", "Which category do you want to print (breakfast, lunch, dinner)?"))
        return CheckResult.wrong("Your program should ask the user about the meal category to print: \"(breakfast, lunch, dinner)?\"");

      if (!co.input("dinner", "Category: dinner", "Name: soup", "Ingredients:", "potato",
              "rice", "mushrooms"))
        return CheckResult.wrong("Wrong \"show\" output for a saved meal.");

      if (!co.inputNext("What would you like to do (add, show, exit)?"))
        return CheckResult.wrong("Your program should ask the user about the required action: \"(add, show, exit)?\"");

      if (!co.input("exit", "Bye!"))
        return CheckResult.wrong("Your output should contain \"Bye!\"");

      if (!co.programIsFinished())
        return CheckResult.wrong("The application didn't exit.");
    } catch (Exception e) {
      return CheckResult.wrong("An exception was thrown while testing - "+e);
    }

    return CheckResult.correct();
  }

}


class CheckOutput {
  private TestedProgram main = new TestedProgram();
  private int position = 0;
  private boolean caseInsensitive = true;
  private boolean trimOutput = true;
  private List<String> arguments = new ArrayList<>();
  private boolean isStarted = false;
  private String lastOutput = "";

  private boolean checkOutput(String outputString, String... checkStr) {
    int searchPosition = position;
    for (String cStr : checkStr) {
      String str = caseInsensitive ? cStr.toLowerCase() : cStr;
      int findPosition = outputString.indexOf(str, searchPosition);
      if (findPosition == -1) return false;
      if (!outputString.substring(searchPosition, findPosition).isBlank()) return false;
      searchPosition = findPosition + str.length();
    }
    position = searchPosition;
    return true;
  }

  public boolean start(String... checkStr) {
    if (isStarted)
      return false;
    var outputString = main.start(arguments.toArray(new String[]{}));
    lastOutput = outputString;
    if (trimOutput) outputString = outputString.trim();
    if (caseInsensitive) outputString = outputString.toLowerCase();
    isStarted = true;
    return checkOutput(outputString, checkStr);
  }

  public void stop() {
    main.stop();
  }

  public boolean input(String input, String... checkStr) {
    if (main.isFinished()) return false;
    String outputString = main.execute(input);
    lastOutput = outputString;
    if (trimOutput) outputString = outputString.trim();
    if (caseInsensitive) outputString = outputString.toLowerCase();
    position = 0;
    return checkOutput(outputString, checkStr);
  }

  public boolean inputNext(String... checkStr) {
    String outputString = lastOutput;
    if (trimOutput) outputString = outputString.trim();
    if (caseInsensitive) outputString = outputString.toLowerCase();
    return checkOutput(outputString, checkStr);
  }

  public String getNextOutput(String input) {
    if (main.isFinished()) return "";
    String outputString = main.execute(input);
    lastOutput = outputString;
    position = 0;
    return outputString;
  }

  public String getLastOutput() {
    return lastOutput;
  }

  public boolean programIsFinished() {
    return main.isFinished();
  }

  public void setArguments(String... arguments) {
    this.arguments = Arrays.stream(arguments).toList();
  }

  public void setCaseInsensitive(boolean caseInsensitive) {
    this.caseInsensitive = caseInsensitive;
  }

  public void setTrimOutput(boolean trimOutput) {
    this.trimOutput = trimOutput;
  }
}