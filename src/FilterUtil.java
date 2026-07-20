/*
this is a global class which can be used to filter any list of objects obtained from any of the getall() methods
<T> is a generic type parameter, so it works with any type of object
Predicate can be a bit confusing so here's an example (of it being used from another class):

TitleDAO titleDAO = new TitleDAO();
FilterUtil.filter(titleDAO.getAllTitles(), t -> t.getGenre().equals("Fiction")); // shows all fiction titles

In short, the t -> declares a Title t which is used for each element in the ArrayList, and then the condition is applied (there's technically a bit more to it but it doesn't matter)

This technically isn't a direct filter with SQL, but this is easier than creating a million different SQL queries for every possible filter combination,
and it also allows for more complex filtering (like filtering by multiple conditions) which would be a pain to do with SQL
*/

import java.util.ArrayList;
import java.util.function.Predicate;

public class FilterUtil {
    public static <T> ArrayList<T> filter(ArrayList<T> items, Predicate<T> condition) {
        ArrayList<T> filteredItems = new ArrayList<>();
        for (T item : items) {
            if (condition.test(item)) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    // multiple-condition version (could be useful), keep in mind that multiple
    public static <T> ArrayList<T> filter(ArrayList<T> items, ArrayList<Predicate<T>> conditions) {
        ArrayList<T> filteredItems = new ArrayList<>();
        for (T item : items) {
            boolean allConditionsMet = true;
            for (Predicate<T> condition : conditions) {
                if (!condition.test(item)) {
                    allConditionsMet = false;
                    break;
                }
            }
            if (allConditionsMet) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }
}
