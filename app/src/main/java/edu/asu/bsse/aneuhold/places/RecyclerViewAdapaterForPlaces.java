package edu.asu.bsse.aneuhold.places;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Copyright 2019 Anton G Neuhold Jr,
 *
 * This software is the intellectual property of the author, and can not be
 * distributed, used, copied, or reproduced, in whole or in part, for any
 * purpose, commercial or otherwise. The author grants the ASU Software
 * Engineering program the right to copy, execute, and evaluate this work for
 * the purpose of determining performance of the author in coursework, and for
 * Software Engineering program evaluation, so long as this copyright and
 * right-to-use statement is kept in-tact in such use. All other uses are
 * prohibited and reserved to the author.<br>
 * <br>
 *
 * Purpose: An adapter for the RecyclerView in the MainActivity.
 *
 * SER 423
 * see http://quay.poly.asu.edu/Mobile/
 * @author Anton Neuhold mailto:aneuhold@asu.edu
 *         Software Engineering
 * @version November 10, 2019
 */
public class RecyclerViewAdapaterForPlaces extends RecyclerView.Adapter {
  public String[] placeNames;

  // Provide a reference to the views for each data item
  // Complex data items may need more than one view per item, and
  // you provide access to all the views for a data item in a view holder
  public static class PlaceViewHolder extends RecyclerView.ViewHolder {

    // each data item is just a string in this case
    public CardView cardView;
    public TextView textView;
    public PlaceViewHolder(CardView v) {
      super(v);
      cardView = v;
      textView = cardView.findViewById(R.id.textView);
    }
  }

  /**
   * Constructor that can temporarily be used while an AsyncPlacesConnect request is being made
   * to retrieve the actual data needed for this RecyclerViewAdapterForPlaces object.
   */
  public RecyclerViewAdapaterForPlaces() {
    this.placeNames = new String[]{"Loading places..."};
  }

  public RecyclerViewAdapaterForPlaces(String[] placeNames) {
    this.placeNames = placeNames;
  }

  // Create new views (invoked by the layout manager)
  @Override
  public RecyclerViewAdapaterForPlaces.PlaceViewHolder onCreateViewHolder(ViewGroup parent,
                                                                          int viewType) {
    // create a new view
    CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
        .inflate(R.layout.place_card_view, parent, false);

    return new PlaceViewHolder(cardView);
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    // - get element from your dataset at this position
    // - replace the contents of the view with that element
    PlaceViewHolder placeViewHolder = (PlaceViewHolder) holder;
    placeViewHolder.textView.setText(placeNames[position]);
  }

  // Return the size of your dataset (invoked by the layout manager)
  @Override
  public int getItemCount() {
    return placeNames.length;
  }
}
