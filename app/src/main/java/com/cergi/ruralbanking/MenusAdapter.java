package com.cergi.ruralbanking;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cergi.ruralbanking.account.AccountActivity;
import com.cergi.ruralbanking.transfer.MoneyTransferActivity;

import java.util.List;

/**
 * Created by david.dzakpasu on 14/10/2016.
 */

public class MenusAdapter extends RecyclerView.Adapter<MenusAdapter.MyViewHolder> {
    private Context mContext;
    private List<MenuCard> itemList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;
        public MenuCard card;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.tvName);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);

        }
    }

    public MenusAdapter(Context mContext, List<MenuCard> itemList) {
        this.mContext = mContext;
        this.itemList = itemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menucard, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final MenuCard menu = itemList.get(position);
        holder.title.setText(menu.getLabel());

        // loading album cover using Glide library
        Glide.with(mContext).load(menu.getThumbnail()).into(holder.thumbnail);

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                switch (menu.getLabel()) {
                    case "OUVERTURE DE COMPTE":
                        Safe.Matricule = null;
                        i = new Intent(mContext, AccountActivity.class);
                        mContext.startActivity(i);
                        break;
                    case "VERSEMENT":
                        i = new Intent(mContext, VersementActivity.class);
                        mContext.startActivity(i);
                        break;
                    case "RETRAIT":
                        i = new Intent(mContext, RetraitActivity.class);
                        mContext.startActivity(i);
                        break;
                    case "TRANSFERT D'ARGENT":
                        i = new Intent(mContext, MoneyTransferActivity.class);
                        mContext.startActivity(i);
                        break;
                    case "DEMANDE DE CREDIT":
                        i = new Intent(mContext, DemandeCreditActivity.class);
                        mContext.startActivity(i);
                        break;
                    case "VIREMENT":
                        break;
                    case "CHANGE":
                        break;
                }
//                Toast.makeText(mContext, "" + menu.getLabel(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

}
