package com.example.photosandroid;

import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photosandroid.R;
import com.example.photosandroid.model.Album;
import com.example.photosandroid.model.Photo;
import com.example.photosandroid.model.Tag;
import com.example.photosandroid.ui.SearchResultAdapter;
import com.example.photosandroid.utils.PhotoManager;
import com.example.photosandroid.utils.SerializationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchActivity extends AppCompatActivity {

    private AutoCompleteTextView tagInput1, tagInput2;
    private RadioGroup searchModeGroup;
    private RecyclerView resultRecyclerView;
    private TextView resultTitle;
    private static final String FILE_NAME = "albums.ser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Search Photos");
        }


        tagInput1 = findViewById(R.id.tagInput1);
        tagInput2 = findViewById(R.id.tagInput2);
        searchModeGroup = findViewById(R.id.searchModeGroup);
        resultRecyclerView = findViewById(R.id.resultRecyclerView);
        resultTitle = findViewById(R.id.resultTitle);
        resultRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        ArrayList<Album> albums = SerializationUtil.load(this, FILE_NAME);
        Set<String> tagSuggestions = new HashSet<>();

        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                for (Tag tag : photo.getTags()) {
                    tagSuggestions.add(tag.getName().toLowerCase(Locale.ROOT) + ":" + tag.getValue());
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(tagSuggestions));
        tagInput1.setAdapter(adapter);
        tagInput2.setAdapter(adapter);

        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> {
            String raw1 = tagInput1.getText().toString().trim().toLowerCase(Locale.ROOT);
            String raw2 = tagInput2.getText().toString().trim().toLowerCase(Locale.ROOT);
            boolean useAnd = ((RadioButton)findViewById(R.id.andRadio)).isChecked();

            Tag tag1 = parseTag(raw1);
            Tag tag2 = parseTag(raw2);

            List<Photo> results = new ArrayList<>();

            for (Album album : albums) {
                for (Photo photo : album.getPhotos()) {
                    Set<Tag> photoTags = photo.getTags().stream()
                            .map(t -> new Tag(t.getName().toLowerCase(), t.getValue().toLowerCase()))
                            .collect(Collectors.toSet());

                    boolean has1 = tag1 != null && photoTags.contains(tag1);
                    boolean has2 = tag2 != null && photoTags.contains(tag2);

                    boolean match = useAnd
                            ? (tag1 != null && tag2 != null && has1 && has2)
                            : (tag1 != null && has1) || (tag2 != null && has2);

                    if (match && !results.contains(photo)) {
                        results.add(photo);
                    }
                }
            }

            if (!results.isEmpty()) {
                resultTitle.setVisibility(TextView.VISIBLE);
                resultRecyclerView.setVisibility(RecyclerView.VISIBLE);
                Map<Photo, String> albumMap = new HashMap<>();
                for (Album album : albums) {
                    for (Photo photo : album.getPhotos()) {
                        if (results.contains(photo)) {
                            albumMap.put(photo, album.getName());
                        }
                    }
                }

                SearchResultAdapter resultAdapter  = new SearchResultAdapter(this, new ArrayList<>(results), albumMap);
                resultRecyclerView.setAdapter(resultAdapter );

            } else {
                resultTitle.setText("Results: (none found)");
                resultTitle.setVisibility(TextView.VISIBLE);
                resultRecyclerView.setVisibility(RecyclerView.GONE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    private Tag parseTag(String raw) {
        if (!raw.contains(":")) return null;
        String[] parts = raw.split(":", 2);
        if (!parts[0].equals("person") && !parts[0].equals("location")) return null;
        return new Tag(parts[0].trim(), parts[1].trim());
    }
}
//Search by tags, show matching photos