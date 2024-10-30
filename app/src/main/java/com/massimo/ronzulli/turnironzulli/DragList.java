package com.massimo.ronzulli.turnironzulli;

import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.getSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.putSinglePref;
import static com.massimo.ronzulli.turnironzulli.STR.SecurityExtension.removeSinglePref;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.BuildConfig;
import com.massimo.ronzulli.turnironzulli.models.RecyclerViewAdapterCards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

class DragList {
    private ArrayList<String> cardsList1;
    private ArrayList<String> displayList;
    private RecyclerViewAdapterCards recyclerViewAdapterCards;
    private String deletedCard;
    private Context context;
    private int posDeleted=-1;
    private RecyclerView recyclerView1;
    private String TAG="DragList";

    public DragList(ArrayList<String> cardsList,Context context) {
        this.context=context;
        this.cardsList1=new ArrayList<>();
        this.cardsList1.addAll(cardsList);
        displayList=new ArrayList<>();
        displayList.addAll(cardsList);
    }
    public void setRecyclerView(final RecyclerView recyclerView){
        recyclerViewAdapterCards = new RecyclerViewAdapterCards(displayList,context);
        recyclerView1 = recyclerView ;
        recyclerView1.setAdapter(recyclerViewAdapterCards);

        ItemTouchHelper.Callback _ithCallback = new ItemTouchHelper.Callback() {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int startPosition = viewHolder.getAbsoluteAdapterPosition();
                int endPosition = target.getAbsoluteAdapterPosition();
                String startString=cardsList1.get(startPosition);
                putSinglePref(context, String.valueOf(startPosition),cardsList1.get(endPosition),"Cards");
                putSinglePref(context, String.valueOf(endPosition),startString,"Cards");

                // get the viewHolder's and target's positions in your adapter data, swap them
                Collections.swap(displayList, startPosition, endPosition);
                Collections.swap(cardsList1, startPosition, endPosition);

                // and notify the adapter that its dataset has changed
                recyclerViewAdapterCards.notifyItemMoved(startPosition, endPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                posDeleted = viewHolder.getAbsoluteAdapterPosition();
                int mov = this.getMovementFlags(recyclerView1, viewHolder);
                if (direction == ItemTouchHelper.LEFT) {
                    final AlertDialog.Builder mBuilder =new AlertDialog.Builder(context);
                    mBuilder.setTitle(R.string.dialogTitleElementDelete);
                    mBuilder.setMessage(R.string.dialogDescriptionElementDelete);
                    mBuilder.setPositiveButton(R.string.dialogYES, (dialog, which) -> {
                        removeSinglePref(context,cardsList1.get(posDeleted) .concat("_Front"),"Cards");
                        removeSinglePref(context,cardsList1.get(posDeleted) .concat("_Rear"),"Cards");
                        removeSinglePref(context,cardsList1.get(posDeleted) .concat("_Front_rot"),"Cards");
                        removeSinglePref(context,cardsList1.get(posDeleted) .concat("_Rear_rot"),"Cards");
                        removeSinglePref(context, cardsList1.get(posDeleted) .concat( "_QR"), "Cards");
                        removeSinglePref(context, cardsList1.get(posDeleted) .concat( "_Bar"), "Cards");
                        deletedCard = displayList.get(posDeleted);
                        cardsList1.remove(posDeleted);
                        displayList.remove(posDeleted);
                        recyclerViewAdapterCards.notifyItemRemoved(posDeleted);
                        for(int i=0;i<cardsList1.size();i++){
                            putSinglePref(context, String.valueOf(i),cardsList1.get(i),"Cards");
                        }
                        removeSinglePref(context, String.valueOf(cardsList1.size()),"Cards");
                        putSinglePref(context, "Size",String.valueOf(cardsList1.size()),"Cards");

                    });
                    mBuilder.setNegativeButton(R.string.dialogNO, (dialog, which) -> {
                        recyclerViewAdapterCards.notifyItemChanged(posDeleted);
                        dialog.dismiss();
                    });
                    mBuilder.setOnCancelListener(dialogInterface -> {
                        recyclerViewAdapterCards.notifyItemChanged(posDeleted);
                    });
                    mBuilder.show();
                } else {
                    recyclerViewAdapterCards.notifyItemChanged(posDeleted);
                }
            }

            //defines the enabled move directions in each state (idle, swiping, dragging).
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlag=makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,ItemTouchHelper.DOWN | ItemTouchHelper.UP);
                int swipeFlag=makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                AtomicInteger retFlag= new AtomicInteger(makeMovementFlags(dragFlag, swipeFlag));
                return retFlag.get();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(_ithCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView1);

    }
    public void addFilterAutoCompleteTextView(AutoCompleteTextView search_ev){

        search_ev.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0){
                    String textToSearch=charSequence.toString().toLowerCase(Locale.ROOT);
                    displayList.clear();
                    for  (int counter=0;counter<cardsList1.size();counter++) {
                        if (cardsList1.get(counter).toLowerCase(Locale.ROOT).contains(textToSearch)){
                            displayList.add(cardsList1.get(counter));
                        }
                    }
                } else {
                    displayList.clear();
                    displayList.addAll(cardsList1);
                }
                if(recyclerView1!=null) Objects.requireNonNull(recyclerView1.getAdapter()).notifyDataSetChanged();
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    //store the list into "Cards.xml"
    public void storeList(){
        try {
            //get old size value
            String sizeStr = getSinglePref(context,"Size","Cards");
            int size=0;
            if (sizeStr.length()>0){
                size= Integer.parseInt(sizeStr);
            }
            if (size>displayList.size()){
                //removes unnecessary elements
                for (int i=displayList.size()-1 ; i<size;i++){
                    removeSinglePref(context,String.valueOf(i),"Cards");
                }
            }
            putSinglePref(context,"Size",String.valueOf(displayList.size()),"Cards");
            for(int i=0;i<displayList.size();i++){
                putSinglePref(context, String.valueOf(i),displayList.get(i),"Cards");
            }
        } catch (Exception e){
            if (BuildConfig.DEBUG){
                Log.e(TAG, "addFilterAutoCompleteTextView: " + e.getMessage());
                Log.e(TAG, "addFilterAutoCompleteTextView: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }
    //retrieve the list from "Cards.xml" and show it
    @SuppressLint("NotifyDataSetChanged")
    public ArrayList<String> retrieveList() {
        try {
            //get size value
            String sizeStr = getSinglePref(context,"Size","Cards");
            int size=0;
            if (sizeStr.length()>0){
                size= Integer.parseInt(sizeStr);
            }
            cardsList1.clear();
            displayList.clear();
            for(int i=0;i<size;i++){
                String value = getSinglePref(context, String.valueOf(i),"Cards");
                cardsList1.add(value);
                displayList.add(value);
            }
            if(recyclerView1!=null) Objects.requireNonNull(recyclerView1.getAdapter()).notifyDataSetChanged();
            return displayList;
        } catch (Exception e){
            if (BuildConfig.DEBUG){
                Log.e(TAG, "retrieveList: " + e.getMessage());
                Log.e(TAG, "retrieveList: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
        return null;
    }
    //add single card to disk
    public void addCard(String text){
        try {
            //get size value
            String sizeStr = getSinglePref(context,"Size","Cards");
            int size=0;
            if (sizeStr.length()>0){
                size= Integer.parseInt(sizeStr);
            }
            size++;
            cardsList1.add(text);
            displayList.add(text);
            putSinglePref(context,"Size",String.valueOf(displayList.size()),"Cards");
            putSinglePref(context,String.valueOf(displayList.size()-1),displayList.get(size-1),"Cards");
            if(recyclerView1!=null) Objects.requireNonNull(recyclerView1.getAdapter()).notifyItemInserted(size);
        } catch (Exception e){
            if (BuildConfig.DEBUG){
                Log.e(TAG, "addCard: " + e.getMessage());
                Log.e(TAG, "addCard: " + Arrays.toString(e.getStackTrace()));
            } else {
                e.printStackTrace();
            }
        }
    }
    public ArrayList<String> getDisplayList() {
        return displayList;
    }

    public void restoreLastDeletedCard(){
        displayList.add(posDeleted,deletedCard);
        recyclerViewAdapterCards.notifyItemChanged(posDeleted);
    }
}
