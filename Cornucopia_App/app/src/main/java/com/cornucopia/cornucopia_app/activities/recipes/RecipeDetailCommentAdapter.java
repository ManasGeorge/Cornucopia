package com.cornucopia.cornucopia_app.activities.recipes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cornucopia.cornucopia_app.R;
import com.cornucopia.cornucopia_app.model.Recipe;

import java.util.List;

/**
 * Displays the comment text
 */
class RecipeDetailCommentAdapter extends RecyclerView.Adapter<RecipeDetailCommentAdapter.RecipeDetailCommentViewHolder> {
    @NonNull
    private List<Recipe.Comment> comments;

    RecipeDetailCommentAdapter(@NonNull List<Recipe.Comment> comments) {
        this.comments = comments;
    }

    public void updateData(List<Recipe.Comment> list) {
        comments = list;
        notifyDataSetChanged();
    }

    @Override
    public RecipeDetailCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_detail_comment, parent, false);
        return new RecipeDetailCommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeDetailCommentViewHolder holder, int position) {
        Recipe.Comment comment = comments.get(position);
        holder.layoutWithRecipeComment(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class RecipeDetailCommentViewHolder extends RecyclerView.ViewHolder {

        private TextView text;

        RecipeDetailCommentViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.recipe_detail_comment_text);
        }

        private void layoutWithRecipeComment(@NonNull final Recipe.Comment comment) {
            text.setText(comment.getText());
        }
    }
}
