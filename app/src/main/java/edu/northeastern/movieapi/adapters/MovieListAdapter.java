package edu.northeastern.movieapi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;


import edu.northeastern.movieapi.R;
import edu.northeastern.movieapi.model.Movie;

public class MovieListAdapter extends  RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {

    private List<Movie> movies;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public MovieListAdapter(List<Movie> movies) {
        this.movies = movies;
    }


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item_row, parent, false);
        return new MovieViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);

        // Update the layout dynamically
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(holder.constraintLayout);

        // Set the movie-specific attributes
        // Load image resource using Glide or any other image loading library
        Glide.with(holder.itemView)
                .load(movie.getImage())
                .into(holder.imageViewMovie);
        holder.textViewMovieTitle.setText(movie.getTitle());
        holder.textViewContentRating.setText(movie.getContentRating());

        holder.textViewMovieDuration.setText(movie.getRuntimeStr());

        if (movie.getImDbRating() != null && !movie.getImDbRating().isEmpty()) {
            holder.textViewMovieRating.setText(movie.getImDbRating());
            holder.textViewMovieRating.setVisibility(View.VISIBLE);
            holder.textViewMovieRating.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_star_rate_24, 0, 0, 0);
        } else {
            holder.textViewMovieRating.setVisibility(View.GONE);
            holder.textViewMovieRating.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
        }

        // Apply the updated layout
        constraintSet.applyTo(holder.constraintLayout);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraintLayout;
        ImageView imageViewMovie;
        TextView textViewMovieTitle;
        TextView textViewContentRating;
        TextView textViewMovieRating;
        TextView textViewMovieDuration;

        public MovieViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.constraintLayoutMovieItem);
            imageViewMovie = itemView.findViewById(R.id.imageViewMovie);
            textViewMovieTitle = itemView.findViewById(R.id.textViewMovieTitle);
            textViewContentRating = itemView.findViewById(R.id.textViewContentRating);
            textViewMovieRating = itemView.findViewById(R.id.textViewMovieRating);
            textViewMovieDuration = itemView.findViewById(R.id.textViewMovieDuration);

            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
