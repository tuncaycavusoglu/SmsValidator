package com.example.smsvalidator;


import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextServicesManager;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SpellCheckerSession.SpellCheckerSessionListener;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CustomAdapter extends BaseAdapter {

    String countryList[];
    String mesajList[];
    String saatList[];
    LayoutInflater inflter;
    ArrayList<String> list=new ArrayList<String>();//Creating arraylist

    public CustomAdapter(Context applicationContext, String[] countryList, String[] mesajList, String[] saatList) {

        this.countryList = countryList;
        this.mesajList = mesajList;
        this.saatList = saatList;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return countryList.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflter.inflate(R.layout.messaging_layout, null);
        TextView country = (TextView) view.findViewById(R.id.text_gchat_date_me);
        TextView mesaj = (TextView) view.findViewById(R.id.text_gchat_message_me);
        TextView mesaj2 = (TextView) view.findViewById(R.id.text_gchat_message_me2);
        TextView saat = (TextView) view.findViewById(R.id.text_gchat_timestamp_me);
        country.setText(countryList[i]);
        String validText = validation(mesajList[i]);
        highlightTextPart(mesaj,10,"[ ]{2,}");


        mesaj.setMovementMethod(LinkMovementMethod.getInstance());
        if(validText.length() > 0){
            mesaj2.setBackgroundColor(Color.RED);
            mesaj2.setText(validText);
            mesaj2.setVisibility(View.VISIBLE);

        }
        mesaj.setText(mesajList[i]);
        saat.setText(saatList[i]);
        return view;
    }

    private String validation(String mesaj){

        String ResultMsg="";
        //Mesaj içindeki tarih ve tarih saat formatlarını çıkar.
        String Tarih="(\\d\\d:\\d\\d)";
        mesaj=mesaj.replaceAll(Tarih,"*");
         //Mesaj içindeki linkleri yakala
        String Were = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        mesaj=mesaj.replaceAll(Were,"**");
       //Özel kelimeler
        mesaj=mesaj.replace("4.5G", "*");
        mesaj=mesaj.replace("4,5G", "*");
        mesaj=mesaj.replace("A.S.", "*");
        mesaj=mesaj.replace("A.S.", "*");
        mesaj=mesaj.replace("A.Ş.", "*");
        mesaj=mesaj.replace("Mersis No:", "*");

       String Priceregex="(\\d*[\\.,\\,])?\\d+";
        mesaj=mesaj.replaceAll(Priceregex,"*");

        String SpaceCount="[ ]{2,}";
        Pattern pattern=Pattern.compile(SpaceCount);
        Matcher matcher=(pattern.matcher(mesaj));
       //Boşluk kontrolü
        while (matcher.find()) {
            ResultMsg=ResultMsg+ "İki veya daha fazla sayıda boşluk var."+ matcher.start()+"\n";
        }



        List<String> words = Arrays.asList(".", ",", "!",":","?");
        for (int i = 0; i < mesaj.length()-1; i++) {

            String prekarakter = "1";
            if (i > 0) prekarakter = String.valueOf(mesaj.charAt(i - 1));
            String karakter = String.valueOf(mesaj.charAt(i));

            String postkarakter = String.valueOf(mesaj.charAt(i + 1));

            if (words.contains((karakter))) {
                if (!postkarakter.equals(" "))
                    ResultMsg = ResultMsg + "Noktalama işaretinden sonra boşluk olmalıdır.\n";
                if (prekarakter.equals(" "))
                    ResultMsg = ResultMsg + "Noktalama işaretinden önce boşluk olmamalıdır.\n";
            }
        }






        return ResultMsg;
    }


    private void highlightTextPart(TextView textView, int index, String regularExpression) {
        String fullText = textView.getText().toString();
        int startPos = 0;
        int endPos = fullText.length();
        String[] textParts = fullText.split(regularExpression);
        if (index < 0 || index > textParts.length - 1) {
            return;
        }
        if (textParts.length > 1) {
            startPos = fullText.indexOf(textParts[index]);
            endPos = fullText.indexOf(regularExpression, startPos);
            if (endPos == -1) {
                endPos = fullText.length();
            }
        }
        Spannable spannable = new SpannableString(fullText);
        ColorStateList blueColor = new ColorStateList(new int[][] { new int[] {}}, new int[] { Color.BLUE });
        TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(null, Typeface.BOLD_ITALIC, -1, blueColor, null);
        BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Color.GREEN);
        spannable.setSpan(textAppearanceSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(backgroundColorSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
    }
}