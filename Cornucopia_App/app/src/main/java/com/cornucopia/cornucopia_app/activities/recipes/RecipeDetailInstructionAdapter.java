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
 * Displays the instruction number (1...x) and the instruction text
 */
class RecipeDetailInstructionAdapter extends RecyclerView.Adapter<RecipeDetailInstructionAdapter.RecipeDetailInstructionViewHolder> {
    @NonNull
    private List<Recipe.Instruction> instructions;

    RecipeDetailInstructionAdapter(@NonNull List<Recipe.Instruction> instructions) {
        this.instructions = instructions;
    }

    @Override
    public RecipeDetailInstructionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_detail_instruction, parent, false);
        return new RecipeDetailInstructionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeDetailInstructionViewHolder holder, int position) {
        Recipe.Instruction instruction = instructions.get(position);
        holder.layoutWithRecipeInstruction(instruction, position);
    }

    @Override
    public int getItemCount() {
        return instructions.size();
    }

    class RecipeDetailInstructionViewHolder extends RecyclerView.ViewHolder {

        private TextView instructionNumber;
        private TextView text;

        RecipeDetailInstructionViewHolder(View itemView) {
            super(itemView);
            instructionNumber = (TextView) itemView.findViewById(R.id.recipe_detail_instruction_number);
            text = (TextView) itemView.findViewById(R.id.recipe_detail_instruction_text);
        }

        private void layoutWithRecipeInstruction(@NonNull final Recipe.Instruction instruction, int index) {
            instructionNumber.setText(Integer.toString(index + 1));
            text.setText(instruction.getText());
        }
    }
}
