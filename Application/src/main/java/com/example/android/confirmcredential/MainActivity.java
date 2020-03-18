/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.example.android.confirmcredential;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;





/**
 * Main entry point for the sample, showing a backpack and "Purchase" button.
 */
public class MainActivity extends Activity {

    private String category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayAllItems();
    }


    private void displayAllItems(){
        findViewById(R.id.Backpack).setOnClickListener(new View.OnClickListener() {
            Intent intent = new Intent(MainActivity.this, ItemList.class);
            @Override
            public void onClick(View v) {
                category = "Backpack";
                intent.putExtra("Key", category);
                startActivity(intent);
            }
        });

        findViewById(R.id.Pant).setOnClickListener(new View.OnClickListener() {
            Intent intent = new Intent(MainActivity.this, ItemList.class);
            @Override
            public void onClick(View v) {
                category = "Pant";
                intent.putExtra("Key", category);
                startActivity(intent);
            }
        });

        findViewById(R.id.Shoe).setOnClickListener(new View.OnClickListener() {
            Intent intent = new Intent(MainActivity.this, ItemList.class);
            @Override
            public void onClick(View v) {
                category = "Shoe";
                intent.putExtra("Key", category);
                startActivity(intent);
            }
        });

        findViewById(R.id.Accessory).setOnClickListener(new View.OnClickListener() {
            Intent intent = new Intent(MainActivity.this, ItemList.class);
            @Override
            public void onClick(View v) {
                category = "Accessory";
                intent.putExtra("Key", category);
                startActivity(intent);
            }
        });

        findViewById(R.id.Cloth).setOnClickListener(new View.OnClickListener() {
            Intent intent = new Intent(MainActivity.this, ItemList.class);
            @Override
            public void onClick(View v) {
                category = "Cloth";
                intent.putExtra("Key", category);
                startActivity(intent);
            }
        });
    }
}
