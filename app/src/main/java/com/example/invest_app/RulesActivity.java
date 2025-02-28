package com.example.invest_app;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RulesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        String rulesText = "Zasady Korzystania z Aplikacji Inwestycyjnej:\n\n" +
                "1. Rejestracja Konta Użytkowników:\n" +
                "   - Każdy użytkownik musi być pełnoletni \n" +
                "   - Użytkownik musi podać prawdziwe, aktualne i kompleksowe informacje podczas rejestracji.\n" +
                "   - Użytkownik jest odpowiedzialny za zachowanie poufności swojego konta i hasła.\n\n" +
                "2. Akceptowanie Warunków Korzystania:\n" +
                "   - Przed korzystaniem z aplikacji użytkownik musi zaakceptować warunki korzystania, które określają zasady korzystania z platformy.\n\n" +
                "3. Ochrona Danych Osobowych:\n" +
                "   - Dostawcy aplikacji inwestycyjnych są zobowiązani do ochrony danych osobowych zgodnie z obowiązującymi przepisami o ochronie danych, takimi jak RODO w Unii Europejskiej.\n\n" +
                "4. Zabezpieczenia Konta:\n" +
                "   - Użytkownik powinien zabezpieczyć swoje konto hasłem i rozważyć korzystanie z dodatkowych zabezpieczeń.\n\n" +
                "5. Dostęp do Informacji Rynkowych:\n" +
                "   - Aplikacje inwestycyjna zapewnia dostęp do aktualnych informacji rynkowych, analiz, wykresów , które mogą być wykorzystywane w celach inwestycyjnych.\n\n" +
                "6. Wykonywanie Zleceń:\n" +
                "   - Użytkownik może korzystać z aplikacji do składania zleceń wpłaty monet z funduszu inwestycyjnego.\n\n" +
                "7. Opłaty i Prowizje:\n" +
                "   - Przed dokonaniem transakcji użytkownik powinien zrozumieć opłaty i prowizje związane z korzystaniem z aplikacji oraz dokonywaniem transakcji.\n\n" +
                "8. Dostęp do Wsparcia:\n" +
                "   - Aplikacje inwestycyjne często oferują wsparcie klienta, które może pomóc użytkownikom w rozwiązywaniu problemów i udzielać informacji.\n\n" +
                "9. Zgodność z Przepisami Prawnymi:\n" +
                "   - Użytkownik musi korzystać z aplikacji zgodnie z obowiązującymi przepisami prawnymi i regulacjami finansowymi w swoim regionie.\n\n";
        TextView rulesContentTextView = findViewById(R.id.rulesContentTextView);
        rulesContentTextView.setText(rulesText);
    }
}