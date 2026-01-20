import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
import re
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.tree import DecisionTreeClassifier, plot_tree
from sklearn.ensemble import RandomForestClassifier
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix

RED = '\033[91m'
GREEN = '\033[92m'
YELLOW = '\033[93m'
RESET = '\033[0m'

# ==========================================
# 1. DATA LOADING
# ==========================================
print(f"\n{YELLOW}--- STEP 1: LOADING DATASET ---{RESET}")
try:
    full_data = pd.read_csv('malicious_phish.csv')
    data = full_data.sample(10000, random_state=42)
    print(f"Loaded {len(data)} samples.")
except FileNotFoundError:
    print(f"{RED}WARNING: 'malicious_phish.csv' not found. Using generated dummy data.{RESET}")
    data = pd.DataFrame({
        'url': ['google.com', 'secure-login-paypal.com/update', 'youtube.com/watch', 'apple-id-verify.net'] * 1000,
        'type': ['benign', 'phishing', 'benign', 'phishing'] * 1000
    })

data['label'] = data['type'].apply(lambda x: 0 if x == 'benign' else 1)

# ==========================================
# 2. FEATURE ENGINEERING
# ==========================================
print(f"\n{YELLOW}--- STEP 2: PREPROCESSING ---{RESET}")

def aggressive_tokenizer(url):
    # Split by standard delimiters and remove empty strings
    tokens = re.split(r'[/\-.]', url) 
    return [t for t in tokens if t and t not in ['www', 'http', 'https', 'com']]

print("Vectorizing URLs (TF-IDF)...")
vectorizer = TfidfVectorizer(
    tokenizer=aggressive_tokenizer,
    token_pattern=None, 
    ngram_range=(1, 2),
    max_features=5000
)

X = vectorizer.fit_transform(data['url'])
y = data['label']

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42, stratify=y)

# ==========================================
# 3. TRAINING
# ==========================================
print(f"\n{YELLOW}--- STEP 3: TRAINING ---{RESET}")

print("1. Training Visual Model (Decision Tree)...")
viz_model = DecisionTreeClassifier(max_depth=4, criterion='entropy', random_state=42)
viz_model.fit(X_train, y_train)

print("2. Training Random Forest (Production Engine)...")
perf_model = RandomForestClassifier(n_estimators=100, n_jobs=-1, random_state=42)
perf_model.fit(X_train, y_train)

# ==========================================
# 4. EVALUATION & VISUALIZATION
# ==========================================
print(f"\n{YELLOW}--- STEP 4: RESULTS & DIAGRAMS ---{RESET}")
predictions = perf_model.predict(X_test)
acc = accuracy_score(y_test, predictions) * 100

print(f"FINAL ACCURACY: {GREEN}{acc:.2f}%{RESET}")
print("\nClassification Report:\n", classification_report(y_test, predictions, target_names=['Safe', 'Malicious']))

# --- VISUALIZATION 1: CONFUSION MATRIX ---
plt.figure(figsize=(8, 6))
cm = confusion_matrix(y_test, predictions)
sns.heatmap(cm, annot=True, fmt='d', cmap='Blues', xticklabels=['Pred Safe', 'Pred Malicious'], yticklabels=['Actual Safe', 'Actual Malicious'])
plt.title("Confusion Matrix")
plt.show()

# --- VISUALIZATION 2: FEATURE IMPORTANCE ---
print("Generating Feature Importance Chart...")
importances = perf_model.feature_importances_
indices = np.argsort(importances)[::-1]
feature_names = vectorizer.get_feature_names_out()

top_n = 20
top_features = [feature_names[i] for i in indices[:top_n]]
top_importances = importances[indices[:top_n]]

plt.figure(figsize=(12, 6))
sns.barplot(x=top_importances, y=top_features, palette="viridis")
plt.title("Top 20 Most Dangerous Keywords (Model Logic)")
plt.xlabel("Importance Score")
plt.ylabel("Keyword/Token")
plt.show()

# --- VISUALIZATION 3: DECISION TREE ---
plt.figure(figsize=(25, 12))
plot_tree(viz_model, feature_names=feature_names, class_names=['SAFE', 'MALICIOUS'], filled=True, rounded=True, fontsize=10)
plt.title("Decision Tree Flow (Automata Logic)")
plt.show()

# ==========================================
# 5. LIVE DEMO
# ==========================================
print("\n==============================================")
print(f"      {RED}LIVE PHISHING DETECTOR READY{RESET}")
print("==============================================")

while True:
    user_url = input(f"\n{YELLOW}Enter URL to scan (or 'exit'): {RESET}")
    if user_url.lower() == 'exit': 
        break
    
    # 1. Vectorize
    vec_input = vectorizer.transform([user_url])
    
    # 2. Get breakdown
    tokens_found = aggressive_tokenizer(user_url)
    
    # 3. Predict
    prob = perf_model.predict_proba(vec_input)[0]
    is_malicious = prob[1] > 0.5
    
    # 4. Output
    print(f"Analysis for: {user_url}")
    print(f"Tokens saw: {tokens_found}")
    
    if is_malicious:
        print(f"RESULT: {RED}🔴 MALICIOUS{RESET} (Confidence: {prob[1]*100:.1f}%)")
    else:
        print(f"RESULT: {GREEN}🟢 SAFE{RESET} (Confidence: {prob[0]*100:.1f}%)")
    print("-" * 30)