# 📸 Ecoceipt — Smart Receipt Scanner & AI-Powered Stock Recommender

Ecoceipt is a Kotlin-based Android app that empowers MSMEs to digitize paper receipts using OCR, extract structured sales data with LLM, and receive AI-powered stock recommendations — all aimed at minimizing overstocking and reducing food waste for a more sustainable future. 🌱

---

## 🌍 Sustainable Impact

Overstocking contributes to unnecessary food waste, which releases harmful greenhouse gases and strains local economies. Ecoceipt empowers small businesses to only stock what’s needed — using AI and data-driven decisions for a greener planet. 🌏

---

## 🧠 Features

- 📷 **OCR Receipt Scanner**  
  Take a photo of your receipt and extract raw text using Google ML Kit OCR.

- 🤖 **LLM-Powered Receipt Formatter**  
  Automatically clean, structure, and interpret receipt text using LLM to extract item names, quantities, prices, and total.

- 📊 **AI-Based Stock Recommendations**  
  Get weekly insights on what to restock or scale down based on your sales patterns, helping reduce overstocking and environmental impact.  

- 💾 **Smart Data Storage**  
  Store structured receipt data in Firebase Firestore for easy retrieval and analysis.

---

## 💡 Tech Stack

| Area             | Tech Used                        |
|------------------|----------------------------------|
| Language         | Kotlin                           |
| UI               | Jetpack Compose + Material3      |
| OCR              | Google ML Kit (Text Recognition) |
| LLM Integration  | Gemini API                       |
| Database         | Firebase Firestore               |
| Architecture     | MVVM + StateFlow                 |

> *Depends on your actual implementation

---

## 🚀 Getting Started

1. **Clone the Repo**
   ```bash
   git clone https://github.com/oxqlion/ecoceipt.git

2. **Open in Android Studio**

3. **Set up API Keys**
    - Firebase (Google Services JSON)
    - OCR (already part of Google ML Kit)
    - LLM API (e.g., OpenAI or Gemini)

4. **Run the App**

    Use emulator or physical device (Camera permission required)

---

## 🎨 Assets
  - App Icon: self made using Gimp
  - Slides presentation: Canva (free version)

---

## 🧠 Team Members
  - Rafi Abhista Naya
  - Samuel Miracle Kristanto
  - Hayya U

---

## 📄 License

This project is built for educational and hackathon purposes.

---

## 🙌 Acknowledgements
  
  - Google ML Kit v2: https://developers.google.com/ml-kit/vision/text-recognition/v2
  - Gemini API: https://ai.google.dev/gemini-api/docs
  - Firebase Firestore: https://firebase.google.com/docs/android/setup
  - 🟥 Youtube tutorials
    - Kotlin Jetpack Compose ML Kit: https://youtu.be/wCADCaeS8-A?si=DLoGTe1VTb7b2Hw9
    - Kotlin Jetpack Compose Custom Font: https://youtu.be/ordh8hpMHFA?si=aZtsd5cA9wLaZxcD
    - Kotlin Jetpack Compose Firebase: https://youtu.be/zCIfBbm06QM?si=xa3u12eSGa-d2G9u
    - Kotlin Jetpack Compose Navigation: https://youtu.be/jcKxC6SK4Us?si=7Rfl_DZOlNdyLqf4
    - Kotlin Jetpack Compose Retrofit: https://youtu.be/w6TIg_LzIxU?si=BDkLz_mtW2Re-RKq
   
  - 🟧 Stackoverflow
    - https://stackoverflow.com/questions/65914568/image-upload-issue-with-retrofit-for-android-10-kotlin
    - https://stackoverflow.com/questions/76084699/error-type-mismatch-inferred-type-is-liststring-but-mutableliststring-was
    - https://stackoverflow.com/questions/74216853/cannot-access-class-com-google-common-util-concurrent-listenablefuture-in-flut
    - https://stackoverflow.com/questions/44998051/cannot-create-an-instance-of-class-viewmodel
    - https://stackoverflow.com/questions/38525403/kotlin-android-print-to-console
   
---

## 📖 References & Data Collecting 
  - https://en.antaranews.com/amp/news/366657/indonesias-655-mln-msmes-absorb-119-mln-workers-govt
  - https://djpb.kemenkeu.go.id/kppn/curup/id/data-publikasi/artikel/2885-umkm-hebat,-perekonomian-nasional-meningkat.html
  - https://lppm.itb.ac.id/wp-content/uploads/sites/55/2021/09/Deck-Karsa-Loka-Vol.011-ITB-170921.pdf
  - https://www.dealstreetasia.com/mpayments/
  - https://jurnal.tiga-mutiara.com/index.php/jimi/article/view/170
  - https://kalsel.antaranews.com/berita/262470/indonesia-to-bring-30-mln-msmes-into-digital-ecosystem-by-2024?m=false
  - https://www.researchandmarkets.com/reports/5997169/indonesia-financial-technology-services-market
  - https://www.linkedin.com/pulse/indonesias-fintech-boom-look-ahead-2024-beyond-59ilc/


