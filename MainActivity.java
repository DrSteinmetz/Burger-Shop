package com.example.burgershop;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private String mealSize;
    private TextView totalPrice_txt;
    private int burgerPrice=0,friesPrice=0,toppingsPrice=0;
    String totalPricePart1,nis;
    ImageButton orderBtn;
    CheckBox baconCb,cheeseCb,tomatoCb,lettuceCb;
    String [] drinks;
    int numDrinks=0;
    int[] drinksPrices ;
    TimePicker time;
    boolean isTimeSelected=false;
    String hour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** <-------- Makes the keyboard stay closed when opening the app --------> */
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

/** <-------- Makes the background stay still when the keyboard opens --------> */
        getWindow().setBackgroundDrawableResource(R.drawable.main_background);

        Button timeButton=findViewById(R.id.time_btn);

        drinks=new String[5];
        drinks[0]=getString(R.string.coke);
        drinks[1]=getString(R.string.sprite);
        drinks[2]=getString(R.string.soda);
        drinks[3]=getString(R.string.lemonade);
        drinks[4]=getString(R.string.water);

        final RelativeLayout relativeLayoutToppings=findViewById(R.id.relative_topping_layout);

        orderBtn= findViewById(R.id.submit_btn);
        orderBtn.setEnabled(false);

        final ScrollView scrollViewMain=findViewById(R.id.main_scroll_view);

        totalPrice_txt=findViewById(R.id.total_price);
        totalPricePart1=getString(R.string.total_price_part1);
        nis=getString(R.string.nis);

        String [] mealSizeStr ={getString(R.string.choose_size),getString(R.string.small_meal),getString(R.string.regular_meal),getString(R.string.large_meal),getString(R.string.huge_meal)};
        Spinner spinner=findViewById(R.id.meal_spn);
        //Setting first row of spinner to show only once
        ArrayAdapter<CharSequence> mealSizeAdapter= new ArrayAdapter<CharSequence>(this, R.layout.spinner_text, mealSizeStr ){
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {

                View v = null;

                if (position == 0) {
                    TextView tv = new TextView(getContext());
                    tv.setHeight(0);
                    tv.setVisibility(View.GONE);
                    v = tv;
                }
                else {

                    v = super.getDropDownView(position, null, parent);
                }

                parent.setVerticalScrollBarEnabled(false);
                return v;
            }
        };
        mealSizeAdapter.setDropDownViewResource(R.layout.simple_spinner_dorpdownn);

        spinner.setAdapter(mealSizeAdapter);

        //Setting meal size spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mealSize=parent.getItemAtPosition(position).toString();
                if(position>0) {
                    switch (position){
                        case 1:
                            burgerPrice=34;
                            break;
                        case 2:
                            burgerPrice=47;
                            break;
                        case 3:
                            burgerPrice=58;
                            break;
                        default:
                            burgerPrice=67;
                            break;
                    }
                    relativeLayoutToppings.setVisibility(View.VISIBLE);
                    updateTotalPrice();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        }) ;

        baconCb=findViewById(R.id.bacon_cb);
        cheeseCb=findViewById(R.id.cheese_cb);
        tomatoCb=findViewById(R.id.tomato_cb);
        lettuceCb=findViewById(R.id.lettuce_cb);
        baconCb.setOnCheckedChangeListener(this);
        cheeseCb.setOnCheckedChangeListener(this);
        lettuceCb.setOnCheckedChangeListener(this);
        tomatoCb.setOnCheckedChangeListener(this);

        RadioGroup fries_radio=findViewById(R.id.radio_btns);
        //Radio group listener for fries
        fries_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.radio_yes) {
                    friesPrice=16;


                }
                else if(checkedId==R.id.radio_no) {
                    friesPrice=0;
                }
                updateTotalPrice();
            }
        });

        final EditText numMeals_txt=findViewById(R.id.num_meals);
        final LinearLayout linearLayout=findViewById(R.id.linear_bags);
        final HorizontalScrollView scrollView=findViewById(R.id.scroll_bottom);
        Button drinksButton=findViewById(R.id.sauces_btn);

        //Dynamically adding radio buttons for drinks selection
        drinksButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                linearLayout.removeAllViews();
                scrollView.setVisibility(View.GONE);

                String numMealsStr=numMeals_txt.getText().toString();
                float scale = getResources().getDisplayMetrics().density;

                try  {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                } catch (Exception e) {}

                if (numMealsStr.length()>0) {
                    numDrinks = Integer.parseInt(numMealsStr);
                }
                if(numDrinks>0) {
                    drinksPrices = new int[numDrinks];
                    scrollView.setVisibility(View.VISIBLE);
                }

                resetDrinksPrices();
                updateTotalPrice();

                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMarginStart((int)scale*5);
                layoutParams.setMarginEnd((int)scale*3);

                for(int i=1;i<=numDrinks;i++)
                {
                    RadioGroup radioGroup=new RadioGroup(MainActivity.this);
                    radioGroup.setLayoutParams(layoutParams);
                    radioGroup.setId(i+10);
                    for(int j=0;j<5;j++)
                    {
                        RadioButton radioButton =new RadioButton(MainActivity.this);
                        radioButton.setLayoutParams(layoutParams);
                        radioButton.setText(drinks[j]);
                        radioButton.setTextColor(getResources().getColor(R.color.white));
                        radioButton.setButtonDrawable(R.drawable.radio_button_selector);
                        radioButton.setTextSize(20);
                        radioButton.setId(j+1);
                        radioGroup.addView(radioButton);
                    }

                    // Setting listener to evey group button created dynamically
                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            int index=group.getId()-11; // Group buttons id starts from 11
                            switch (checkedId) {
                                case 1:
                                    drinksPrices[index]=11;
                                    break;
                                case 2:
                                    drinksPrices[index]=9;
                                    break;
                                case 3:
                                    drinksPrices[index]=6;
                                    break;
                                case 4:
                                    drinksPrices[index]=7;
                                    break;
                                case 5:
                                    drinksPrices[index]=5;
                                    break;
                            }
                            updateTotalPrice();
                        }
                    });
                    linearLayout.addView(radioGroup);
                }
                if(burgerPrice!=0)
                    scrollViewMain.scrollTo(0, (int) orderBtn.getY());
            }
        });

        // Printing summary in a dialog
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderBtn.isEnabled())
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_succes, null);

                    builder.setView(view);

                    TextView orderDialogTvContent =(TextView)view.findViewById(R.id.order_contents_tv),
                            hereIsYourOrder = (TextView)view.findViewById(R.id.here_is_your_order_tv),
                            orderTotalDialog =(TextView)view.findViewById(R.id.price_tv);

                    String orderSummary=getBurgerSize()+getToppings()+getFries()+getDrinks()+getString(R.string.time_for_pick_up)+hour;
                    orderDialogTvContent.setText(orderSummary);
                    orderTotalDialog.setText(totalPrice_txt.getText().toString());

                    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, getString(R.string.success_dialog_msg), Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    final AlertDialog alertDialog = builder.create();
                    Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawableResource(R.color.dialogBackground);

                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
                            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.blue));
                        }
                    });
                    alertDialog.show();
                }
            }
        });
        // Dialog for selecting time of pick up
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_time, null);
                builder.setView(view);

                final TextView timeTv=(TextView)view.findViewById(R.id.time_tv_dialog);
                time =(TimePicker) view.findViewById(R.id.time);

                time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        String h;
                        String m;
                        String hourTv;
                        if(hourOfDay<10)
                            h="0"+hourOfDay;
                        else
                            h=hourOfDay+"";

                        if(minute<10)
                            m="0"+minute;
                        else
                            m=minute+"";
                        hourTv =getString(R.string.time_selected) +h + ":" + m;
                        timeTv.setText(hourTv);
                    }
                });


                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String h;
                        String m;
                        if(time.getCurrentHour()<10)
                            h="0"+time.getCurrentHour();
                        else
                            h=time.getCurrentHour().toString();

                        if(time.getCurrentMinute()<10)
                            m="0"+time.getCurrentMinute();
                        else
                            m=time.getCurrentMinute().toString();
                        hour = h + ":" + m;
                        Toast.makeText(MainActivity.this, hour+" "+getString(R.string.selected), Toast.LENGTH_SHORT).show();
                        isTimeSelected=true;
                        updateTotalPrice();
                    }
                });

                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, getString(R.string.select_time_again_and_press_ok), Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

                final AlertDialog alertDialog = builder.create();
                Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawableResource(R.color.dialogBackground);

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.blue));
                    }
                });
                alertDialog.show();
            }

        });

    }
    @Override
    // Check list listener for the toppings
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        CheckBox checkBox=(CheckBox)buttonView;

        if (isChecked) {
            if(checkBox.getId()==R.id.cheese_cb)
                toppingsPrice+=7;
            else if(checkBox.getId()==R.id.bacon_cb)
                toppingsPrice+=9;
        }
        else {
            if(checkBox.getId()==R.id.cheese_cb)
                toppingsPrice-=7;
            else if(checkBox.getId()==R.id.bacon_cb)
                toppingsPrice-=9;
        }
        updateTotalPrice();
    }

    private void updateTotalPrice()
    {
        int drinksCurrentPrice=sumDrinkPrices();
        if(burgerPrice!=0&&isTimeSelected) {
            String total = totalPricePart1 + (burgerPrice + friesPrice + toppingsPrice+drinksCurrentPrice) + nis;
            totalPrice_txt.setText(total);
            orderBtn.setEnabled(true);
        }
        else if(burgerPrice!=0&&!isTimeSelected)
        {
            totalPrice_txt.setText(getString(R.string.time_not_selected));
            orderBtn.setEnabled(false);
        }
        else if(burgerPrice==0&&isTimeSelected)
        {
            totalPrice_txt.setText(getString(R.string.burger_not_selected));
            orderBtn.setEnabled(false);
        }
        else {
            totalPrice_txt.setText(getString(R.string.none_selected));
            orderBtn.setEnabled(false);
        }
    }
    private String getBurgerSize(){
        switch (burgerPrice){
            case 34:
                return getString(R.string.burger)+" "+getString(R.string.small_meal)+"\n";
            case 47:
                return getString(R.string.burger)+" "+getString(R.string.regular_meal)+"\n";

            case 58:
                return getString(R.string.burger)+" "+getString(R.string.large_meal)+"\n";

            case 67:
                return getString(R.string.burger)+" "+getString(R.string.huge_meal)+"\n";
            default:
                return "";

        }
    }
    private String getToppings()
    {
        StringBuilder toppings=new StringBuilder();
        toppings.append(getString(R.string.toppings));
        toppings.append(": ");
        boolean isToppings=false;
        if (burgerPrice > 0) {


            if (baconCb.isChecked()) {
                toppings.append(getString(R.string.bacon_txt));
                toppings.append(", ");
                isToppings = true;
            }
            if (cheeseCb.isChecked()) {
                toppings.append(getString(R.string.cheese_txt));
                toppings.append(", ");
                isToppings = true;
            }
            if (tomatoCb.isChecked()) {
                toppings.append(getString(R.string.tomato_txt));
                toppings.append(", ");
                isToppings = true;
            }
            if (lettuceCb.isChecked()) {
                toppings.append(getString(R.string.lettuce_txt));
                toppings.append(", ");
                isToppings = true;
            }

            if (isToppings) {
                toppings.replace(toppings.length()-2,toppings.length(),"\n");
                return toppings.toString();
            }
            return getString(R.string.no_toppings)+"\n";
        }
        return "";


    }
    private String getFries(){
        if(friesPrice>0)
            return getString(R.string.friesExtra)+"\n";
        return "";



    }
    private void resetDrinksPrices()
    {
        int i;
        if (drinksPrices!=null) {
            for (i=0; i < drinksPrices.length; i++)
                drinksPrices[i] = 0;
        }
    }
    private int sumDrinkPrices(){
        int i,sum=0;
        if (drinksPrices!=null) {
            for (i=0; i < drinksPrices.length; i++)
                sum+=drinksPrices[i];
        }
        return sum;
    }
    private String getDrinks()
    {

        if(drinksPrices!=null)
        {
            boolean isDrink=false;
            int coke=0,sprite=0,soda=0,lemonade=0,water=0;
            StringBuilder drinks=new StringBuilder();
            drinks.append(getString(R.string.drinks));
            drinks.append(": ");
            for (int drinksPrice : drinksPrices) {
                switch (drinksPrice) {
                    case 11:
                        isDrink = true;
                        coke++;
                        break;
                    case 9:
                        isDrink = true;
                        sprite++;
                        break;
                    case 6:
                        isDrink = true;
                        soda++;
                        break;
                    case 7:
                        isDrink = true;
                        lemonade++;
                        break;
                    case 5:

                        isDrink = true;
                        water++;
                        break;
                }
            }
            if(isDrink) {
                if(coke>0) {
                    drinks.append(coke);
                    drinks.append(getString(R.string.x));
                    drinks.append(getString(R.string.coke));
                    drinks.append(", ");
                }
                if(sprite>0) {
                    drinks.append(sprite);
                    drinks.append(getString(R.string.x));
                    drinks.append(getString(R.string.sprite));
                    drinks.append(", ");
                }
                if(lemonade>0) {
                    drinks.append(lemonade);
                    drinks.append(getString(R.string.x));
                    drinks.append(getString(R.string.lemonade));
                    drinks.append(", ");
                }
                if(soda>0) {
                    drinks.append(soda);
                    drinks.append(getString(R.string.x));
                    drinks.append(getString(R.string.soda));
                    drinks.append(", ");
                }
                if(water>0) {
                    drinks.append(water);
                    drinks.append(getString(R.string.x));
                    drinks.append(getString(R.string.water));
                    drinks.append(", ");
                }
                drinks.replace(drinks.length()-2,drinks.length(),"\n");
                return drinks.toString();
            }
        }
        return getString(R.string.no_drinks_selected)+"\n";
    }

}
