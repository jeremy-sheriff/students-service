#!/bin/zsh

# Function to delete an environment variable
delete_env_var() {
    local key="$1"

    if grep -q "^export $key=" ~/.zshrc; then
        # If the variable exists, delete it
        sed -i '' "/^export $key=/d" ~/.zshrc
        echo "Deleted $key from .zshrc"
    else
        echo "$key not found in .zshrc"
    fi
}

# List of environment variables to delete
env_vars=(
    "KEY_CLOAK_CLIENT_ID"
    "KEY_CLOAK_USERNAME"
    "KEY_CLOAK_PASSWORD"
    "KEY_CLOAK_TOKEN_ENDPOINT"
    "KEY_CLOAK_ISSUER_URI"
    "KEY_CLOAK_DB_PASSWORD"
    "KEY_CLOAK_DB_URL"
    "KEY_CLOAK_DB_USERNAME"
    "KEY_CLOAK_USERS_DB"
    "KEY_CLOAK_CORS_ALLOWED_ORIGINS"
    "KEY_CLOAK_COURSE_URL"
    # Add any other environment variables you want to delete here
)

# Loop through each variable and delete it
for var in "${env_vars[@]}"; do
    delete_env_var "$var"
done

# Source the .zshrc file to apply the changes
source ~/.zshrc

echo "All specified environment variables deleted from .zshrc and changes applied."
