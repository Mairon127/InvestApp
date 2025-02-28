package com.example.invest_app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class About extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        String rulesText = "Aplikacja inwestycyjna to narzędzie umożliwiające użytkownikom inwestowanie swoich monet posiadanych na koncie w formie wpłat\n\n"
                + "Aplikacja ta dostarcza dostęp do różnych instrumentów finansowych, analizy rynkowej, portfeli inwestycyjnych i narzędzi do śledzenia wydarzeń ekonomicznych.\n\n"
                + "Użytkownicy mogą dokonywać transakcji oraz zarządzać swoim portfelem za pomocą interfejsu aplikacji.\n\n"
                + "Aplikacja inwestycyjna ma na celu naukę początkujących inwestorów inwestowania w odpowiednie spółki, dlatego powstał prototyp aplikacji do nauki inwestycji w rzeczywistym świecie.\n\n";
        String projectCreationText = "W celu utworzenia projektu wykorzystano źródła:";

        String link1 = "https://github.com/MilesBellum/BlockchainExampleWithJava";
        String link2 = "https://www.geeksforgeeks.org/how-to-build-a-cryptocurrency-tracker-android-app/";
        String link3 = "https://www.exchangerate-api.com/";
        String link4="https://www.youtube.com/playlist?list=PL2cPYwzGtGnsCzwZSENaHojYpR3YiHDM1";
        String link5="https://www.youtube.com/watch?v=QAKq8UBv4GI";
        String link6="https://www.youtube.com/playlist?list=PLFzlb57tNKUMw9BxYvJYlAVvJKzblsb7H";

        projectCreationText += "\n1. " + link1 + "\n2. " + link2 + "\n3. " + link3+ "\n4. " + link4+ "\n5. " + link5+ "\n6. " + link6;

        String combinedText = rulesText + "\n\n" + projectCreationText;

        TextView rulesContentTextView = findViewById(R.id.aboutContentTextView);
        rulesContentTextView.setText(combinedText);

        Button closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> finish());
    }
}
