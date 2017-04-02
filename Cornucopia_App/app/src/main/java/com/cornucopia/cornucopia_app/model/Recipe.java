package com.cornucopia.cornucopia_app.model;

public class Recipe {
    private String recipeName;
    private boolean isFavorited;
    private String prepTime;

    public Recipe(String recipeName, boolean isFavorited, String prepTime) {
        this.recipeName = recipeName;
        this.isFavorited = isFavorited;
        this.prepTime = prepTime;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }

    public String getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(String prepTime) {
        this.prepTime = prepTime;
    }

    public static class Ingredient {
        private String ingredientName;
        private String quantity;

        public Ingredient(String ingredientName, String quantity) {
            this.ingredientName = ingredientName;
            this.quantity = quantity;
        }

        public String getIngredientName() {
            return ingredientName;
        }

        public void setIngredientName(String ingredientName) {
            this.ingredientName = ingredientName;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }
    }

    public static class Instruction {
        private String text;

        public Instruction(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class Comment {
        private String text;
        private String user;

        public Comment(String text, String user) {
            this.text = text;
            this.user = user;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }
    }
}

