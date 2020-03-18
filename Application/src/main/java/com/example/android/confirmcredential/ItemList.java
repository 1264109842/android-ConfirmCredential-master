package com.example.android.confirmcredential;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class ItemList extends Activity {

    //private String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itemlist);

        Comparison_();
    }


    private void Comparison_() {
         String category = getIntent().getStringExtra("Key");
         ImageButton Item_1 = findViewById(R.id.Item1);
         ImageButton Item_2 = findViewById(R.id.Item2);
         TextView Item_11 = findViewById(R.id.Item1_name);
         TextView Item_22 = findViewById(R.id.Item2_name);

        if(category.compareTo("Backpack") == 0){
            Item_1.setImageResource(R.drawable.gucci_backpack);
            Item_2.setImageResource(R.drawable.nf_backpack);
            Item_11.setText(String.format("%s", "Gucci Backpack"));
            Item_22.setText(String.format("%s", "North Face Backpack"));
            ListItems("Gucci", "North Face Backpack");
        }
        else if(category.compareTo("Pant") == 0)
        {
            Item_1.setImageResource(R.drawable.jeans);
            Item_2.setImageResource(R.drawable.sport_pants);
            Item_11.setText(String.format("%s","Stretch Jean"));
            Item_22.setText(String.format("%s", "Sport Pant"));
            ListItems("Stretch Jean", "Sport Pant");
        }
        else if (category.compareTo("Cloth") == 0){
            Item_1.setImageResource(R.drawable.t_shirt);
            Item_2.setImageResource(R.drawable.coat);
            Item_11.setText(String.format("%s","T-Shirt"));
            Item_22.setText(String.format("%s","Snorkel Coat"));
            ListItems("T-Shirt", "Snorkel Coat");
        }
        else if (category.compareTo("Shoe") == 0){
            Item_1.setImageResource(R.drawable.yezzy);
            Item_2.setImageResource(R.drawable.airjardon);
            Item_11.setText(String.format("%s", "Adidas Yeezy Boost"));
            Item_22.setText(String.format("%s","Air Jordan"));
            ListItems("Adidas Yeezy Boost", "Air Jordan");
        }
        else{
            Item_1.setImageResource(R.drawable.neckless);
            Item_2.setImageResource(R.drawable.bracelet);
            Item_11.setText(String.format("%s", "Diamond Necklace"));
            Item_22.setText(String.format("%s", "Bead Bracelet"));
            ListItems("Diamond Necklace", "Bead Bracelet");
        }
    }

    private void ListItems(String string1, String string2){
        final String str1 = string1;
        final String str2 = string2;
        findViewById(R.id.Item1).setOnClickListener(new View.OnClickListener() {
            Intent intent = new Intent(ItemList.this, ItemDetail.class);
            @Override
            public void onClick(View v) {
                String value = str1;
                intent.putExtra("Key", value);
                startActivity(intent);
            }
        });
        findViewById(R.id.Item2).setOnClickListener(new View.OnClickListener() {
            Intent intent = new Intent(ItemList.this, ItemDetail.class);
            @Override
            public void onClick(View v) {
                String value = str2;
                intent.putExtra("Key", value);
                startActivity(intent);
            }
        });
    }
}
