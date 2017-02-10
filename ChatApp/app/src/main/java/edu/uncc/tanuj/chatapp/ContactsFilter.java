package edu.uncc.tanuj.chatapp;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by vinay on 11/19/2016.
 */

public class ContactsFilter extends Filter {
    ContactAdapter adapter;

    List<User> originalList;

    List<User> filteredList;
    String userName;
    public ContactsFilter(ContactAdapter adapter, List<User> originalList) {
        super();
        this.adapter = adapter;
        this.originalList = new LinkedList<>(originalList);
        this.filteredList = new ArrayList<>();
    }
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        filteredList.clear();
        final FilterResults results = new FilterResults();

        if (constraint.length() == 0) {
            filteredList.addAll(originalList);
        } else {
            final String filterPattern = constraint.toString();

            for (final User user : originalList) {
                userName=user.getFirstname()+" "+user.getLastname();
                if (userName.contains(filterPattern)) {
                    filteredList.add(user);
                }
            }
        }
        results.values = filteredList;
        results.count = filteredList.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.filteredUserList.clear();
        adapter.filteredUserList.addAll((ArrayList<User>) results.values);
        adapter.notifyDataSetChanged();
    }
}
