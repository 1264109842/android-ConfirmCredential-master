package com.example.android.confirmcredential;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.security.keystore.UserNotAuthenticatedException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;



public class ItemDetail extends Activity {
    /** Alias for our key in the Android Key Store. */
    private static final String KEY_NAME = "my_key";
    private static final byte[] SECRET_BYTE_ARRAY = new byte[] {1, 2, 3, 4, 5, 6};

    private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 1;

    /**
     * If the user has unlocked the device Within the last this number of seconds,
     * it can be considered as an authenticator.
     */
    private static final int AUTHENTICATION_DURATION_SECONDS = 30;

    private KeyguardManager mKeyguardManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        setItem();
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        Button purchaseButton = (Button) findViewById(R.id.purchase_button);
        if (!mKeyguardManager.isKeyguardSecure()) {
            // Show a message that the user hasn't set up a lock screen.
            Toast.makeText(this,
                    "Secure lock screen hasn't set up.\n"
                            + "Go to 'Settings -> Security -> Screenlock' to set up a lock screen",
                    Toast.LENGTH_LONG).show();
            purchaseButton.setEnabled(false);
            return;
        }
        createKey();
        findViewById(R.id.purchase_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Test to encrypt something. It might fail if the timeout expired (30s).
                tryEncrypt();
            }
        });
    }

    private boolean tryEncrypt() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_NAME, null);
            Cipher cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);

            // Try encrypting something, it will only work if the user authenticated within
            // the last AUTHENTICATION_DURATION_SECONDS seconds.
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            cipher.doFinal(SECRET_BYTE_ARRAY);

            // If the user has recently authenticated, you will reach here.
            showAlreadyAuthenticated();
            return true;
        } catch (UserNotAuthenticatedException e) {
            // User is not authenticated, let's authenticate with device credentials.
            showAuthenticationScreen();
            return false;
        } catch (KeyPermanentlyInvalidatedException e) {
            // This happens if the lock screen has been disabled or reset after the key was
            // generated after the key was generated.
            Toast.makeText(this, "Keys are invalidated after created. Retry the purchase\n"
                            + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            return false;
        } catch (BadPaddingException | IllegalBlockSizeException | KeyStoreException |
                CertificateException | UnrecoverableKeyException | IOException
                | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private void createKey() {
        // Generate a key to decrypt payment credentials, tokens, etc.
        // This will most likely be a registration step for the user when they are setting up your app.
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyGenerator keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    // Require that the user has unlocked in the last 30 seconds
                    .setUserAuthenticationValidityDurationSeconds(AUTHENTICATION_DURATION_SECONDS)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | NoSuchProviderException
                | InvalidAlgorithmParameterException | KeyStoreException
                | CertificateException | IOException e) {
            throw new RuntimeException("Failed to create a symmetric key", e);
        }
    }

    private void showAuthenticationScreen() {
        // Create the Confirm Credentials screen. You can customize the title and description. Or
        // we will provide a generic one for you if you leave it null
        Intent intent = mKeyguardManager.createConfirmDeviceCredentialIntent(null,
                null);
        if (intent != null) {
            startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
            // Challenge completed, proceed with using cipher
            if (resultCode == RESULT_OK) {
                if (tryEncrypt()) {
                    showPurchaseConfirmation();
                }
            } else {
                // The user canceled or didn’t complete the lock screen
                // operation. Go to error/cancellation flow.
                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showPurchaseConfirmation() {
        findViewById(R.id.confirmation_message).setVisibility(View.VISIBLE);
        findViewById(R.id.purchase_button).setEnabled(false);
        setBackButton();
    }

    private void showAlreadyAuthenticated() {
        TextView textView = findViewById(
                R.id.already_has_valid_device_credential_message);
        textView.setVisibility(View.VISIBLE);
        textView.setText(getResources().getQuantityString(
                R.plurals.already_confirmed_device_credentials_within_last_x_seconds,
                AUTHENTICATION_DURATION_SECONDS, AUTHENTICATION_DURATION_SECONDS));
        findViewById(R.id.purchase_button).setEnabled(false);
        setBackButton();
    }

    private void setBackButton(){
        findViewById(R.id.toMain).setVisibility(View.VISIBLE);
        ImageView img = findViewById(R.id.image);
        img.setImageResource(R.drawable.purchase_completed);
        final Intent intent = new Intent(ItemDetail.this, MainActivity.class);
        findViewById(R.id.toMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = findViewById(
                        R.id.already_has_valid_device_credential_message);
                textView.setVisibility(View.INVISIBLE);
                findViewById(R.id.toMain).setVisibility(View.INVISIBLE);
                findViewById(R.id.purchase_button).setEnabled(true);
                findViewById(R.id.purchase_button).setVisibility(View.INVISIBLE);
                findViewById(R.id.confirmation_message).setVisibility(View.INVISIBLE);
                startActivity(intent);
            }
        });
    }

    private void setItem() {
        String value = getIntent().getStringExtra("Key");
        ImageView imag_ = findViewById(R.id.image);
        TextView title_ = findViewById(R.id.title);
        TextView description_  = findViewById(R.id.description);
        TextView price_ = findViewById(R.id.price);

        if(value.compareTo("North Face") == 0) {
            imag_.setImageResource(R.drawable.nf_backpack);
            title_.setText(String.format("%s", "NorthFace Backpack"));
            description_.setText(String.format("%s", "Large main compartment for books and binders"));
            price_.setText(String.format("%s","$100"));
        }
        else if(value.compareTo("Stretch Jean") == 0) {
            imag_.setImageResource(R.drawable.jeans);
            title_.setText(String.format("%s", "Stretch Jean"));
            description_.setText(String.format("%s", "Highest level of stretch for maximum comfort"));
            price_.setText(String.format("%s","$150"));
        }
        else if(value.compareTo("Sport Pant") == 0) {
            imag_.setImageResource(R.drawable.sport_pants);
            title_.setText(String.format("%s", "Sport Pant"));
            description_.setText(String.format("%s", "Faux front fly for a more formal look"));
            price_.setText(String.format("%s","$200"));
        }
        else if(value.compareTo("T-Shirt") == 0) {
            imag_.setImageResource(R.drawable.t_shirt);
            title_.setText(String.format("%s", "T-Shirt"));
            description_.setText(String.format("%s", "Comfortable enough for everyday wear."));
            price_.setText(String.format("%s","$30"));
        }
        else if(value.compareTo("Snorkel Coat") == 0) {
            imag_.setImageResource(R.drawable.coat);
            title_.setText(String.format("%s", "Snorkel Coat"));
            description_.setText(String.format("%s", "Waterproof, breathable, seam-sealed DryVent™ 2L shell"));
            price_.setText(String.format("%s","$400"));
        }
        else if(value.compareTo("Adidas Yeezy Boost") == 0) {
            imag_.setImageResource(R.drawable.yezzy);
            title_.setText(String.format("%s", "Adidas Yeezy Boost"));
            description_.setText(String.format("%s", "Cloud White Non-Reflective"));
            price_.setText(String.format("%s","$450"));
        }
        else if(value.compareTo("Air Jordan") == 0) {
            imag_.setImageResource(R.drawable.airjardon);
            title_.setText(String.format("%s", "Air Jordan"));
            description_.setText(String.format("%s", "Rubber cupsole with encapsulated Air for lightweight cushioning"));
            price_.setText(String.format("%s","$130"));
        }
        else if(value.compareTo("Diamond Necklace") == 0) {
            imag_.setImageResource(R.drawable.neckless);
            title_.setText(String.format("%s", "Diamond Necklace"));
            description_.setText(String.format("%s", "A contemporary floating bail design with a luxurious length chain."));
            price_.setText(String.format("%s","$30000"));
        }
        else if(value.compareTo("Bead Bracelet") == 0) {
            imag_.setImageResource(R.drawable.bracelet);
            title_.setText(String.format("%s", "Bead Bracelet"));
            description_.setText(String.format("%s", "Black snowflake obsidian beads."));
            price_.setText(String.format("%s","$170"));
        }
    }
}
