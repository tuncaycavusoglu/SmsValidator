package com.example.smsvalidator;

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
import android.widget.BaseAdapter;
import android.widget.TextView;
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
        boolean izinIptalvarmi=true;

        String ResultMsg="";
        //Mesaj i??indeki tarih ve tarih saat formatlar??n?? ????kar.
        String Tarih="(\\d\\d:\\d\\d)";
        mesaj=mesaj.replaceAll(Tarih,"*");
         //Mesaj i??indeki linkleri yakala
        String reglink1 = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        mesaj=mesaj.replaceAll(reglink1,"**LINK**");
        String reglink2 = "(www.)?"
        + "[a-zA-Z0-9@:%._\\+~#?&//=]{2,256}\\.[a-z]"
        + "{2,6}\\b([-a-zA-Z0-9@:%._\\+~#?&//=]*)";
        mesaj=mesaj.replaceAll(reglink2,"**LINK**");

        String sen??zin??ptal="iznini iptal edebilirsin";
        String siz??zin??ptal="izninizi iptal edebilirsiniz";
        if( !(mesaj.contains(sen??zin??ptal) || mesaj.contains(siz??zin??ptal)))
        {
            izinIptalvarmi=false;
            ResultMsg=ResultMsg+"??zin iptal metni yoktur, yasal g??nderim de??ilse ekleyiniz.\n";
        }

       //??zel kelimeler
        mesaj=mesaj.replace("4.5G", "*");
        mesaj=mesaj.replace("4,5G", "*");
        mesaj=mesaj.replace("A.S.", "*");
        mesaj=mesaj.replace("A.S.", "*");
        mesaj=mesaj.replace("A.??.", "*");
        mesaj=mesaj.replace("Mersis No:", "*");

       String Priceregex="(\\d*[\\.,\\,])?\\d+";
        mesaj=mesaj.replaceAll(Priceregex,"*");

        String SpaceCount="[ ]{2,}";
        Pattern pattern=Pattern.compile(SpaceCount);
        Matcher matcher=(pattern.matcher(mesaj));
       //Bo??luk kontrol??
        while (matcher.find()) {
            ResultMsg=ResultMsg+ "??ki veya daha fazla say??da bo??luk var."+ matcher.start()+"\n";
        }



        List<String> words = Arrays.asList(".", ",", "!",":","?");
        for (int i = 0; i < mesaj.length()-1; i++) {

            String prekarakter = "1";
            if (i > 0) prekarakter = String.valueOf(mesaj.charAt(i - 1));
            String karakter = String.valueOf(mesaj.charAt(i));

            String postkarakter = String.valueOf(mesaj.charAt(i + 1));

            if (words.contains((karakter))) {
                if (!postkarakter.equals(" "))
                    ResultMsg = ResultMsg + "Noktalama i??aretinden sonra bo??luk olmal??d??r."+mesaj.substring(i,i+5)+"\n";
                if (prekarakter.equals(" "))
                    ResultMsg = ResultMsg + "Noktalama i??aretinden ??nce bo??luk olmamal??d??r."+mesaj.substring(i-5,i)+"\n";
            }
        }

        String linkstr="**LINK**";
        int Index=0;
        while (Index!=-1) {
            int newIndex=mesaj.indexOf(linkstr, Index);
            Index=newIndex;
            if(Index>-1){
                if(mesaj.charAt(Index-1)!=" ".toCharArray()[0]){
                    ResultMsg = ResultMsg + "Linkten ??nce bo??luk olmal??d??r.\n";
                }
                if(mesaj.charAt(Index+8)!=" ".toCharArray()[0]){
                    ResultMsg = ResultMsg + "Linkten sonra bo??luk olmal??d??r.\n";
                }
                Index++;

            }
        }


        if(izinIptalvarmi){
            //senli-sizli uyum kontrol??
            String izin??ptalsizMesaj=mesaj.replace(sen??zin??ptal,"**").replace(siz??zin??ptal,"**")
                    .replace("size ulasilmasini istemiyorsaniz","**");

            int MsgSah??s=-1;
            int ??zinSah??s=-1;
            List<String> ??yeliklist=Arrays.asList("??n??z", "iniz", "unuz", "??n??z");
            List<String> Kisilist=Arrays.asList("siz","sizin","size","sizi");
            List<String> Kisilist2=Arrays.asList("sen","sana","seni");
            String[] splitmsglist= izin??ptalsizMesaj.split(" ");

            Boolean devam=true;
            int i=0;
            while(devam && i< splitmsglist.length) {
               if( Kisilist.contains(splitmsglist[i])) {
                   MsgSah??s=1;
                   devam=false;
               }
               else{
                   i++;
               }

            }
            if(izin??ptalsizMesaj.contains("??n??z")||izin??ptalsizMesaj.contains("iniz")||izin??ptalsizMesaj.contains("unuz")||izin??ptalsizMesaj.contains("??n??z")){
                MsgSah??s=1;
            }

            if(MsgSah??s!=1){

                Boolean devam2=true;
                int i2=0;
                while(devam2 && i2< splitmsglist.length) {
                    if( Kisilist2.contains(splitmsglist[i2])) {
                        MsgSah??s=2;
                        devam2=false;
                    }
                    else{
                        i2++;
                    }

                }
            }

            //??zin iptal metninin sen siz durumu yorumlan??yor.

            if(mesaj.contains(siz??zin??ptal)){
                ??zinSah??s=1;
            }
            else if (mesaj.contains(sen??zin??ptal)){
                ??zinSah??s=2;
            }

            if(MsgSah??s==1 && ??zinSah??s==2){
                ResultMsg = ResultMsg + "Mesaj metni sizli iken, izin iptal metni senli olmamal??d??r.\n";
            }
            else if (MsgSah??s==2 && ??zinSah??s==1){
                ResultMsg = ResultMsg + "Mesaj metni senli iken, izin iptal metni sizli olmamal??d??r.\n";
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