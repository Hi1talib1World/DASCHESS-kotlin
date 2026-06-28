# DAS CHESS

DAS CHESS is a modern, clean, and distraction-free Chess application built for Android using Kotlin. It aims to provide a high-quality experience for both ranked play and AI training.

## 🚀 Features

-   **Interactive Dashboard**: A personalized home screen showing user stats (rating, rank) and recent match history.
-   **Recent Matches**: A dynamic list of your latest games with opponent details and results, implemented using `RecyclerView`.
-   **Fully Functional Chessboard**: Custom-built `ChessboardView` supporting piece movement, validation, and visual feedback.
-   **Game Logic**: In-game features including move undo, resignation, and automated game timers for both players.
-   **Daily Puzzles**: Challenges to sharpen your skills, updated daily.
-   **Clean Navigation**: Seamlessly switch between Play, History, Puzzles, and Profile via a modern Bottom Navigation Bar.

## 🛠 Architecture & Technologies

-   **Language**: 100% [Kotlin](https://kotlinlang.org/)
-   **Architecture**: 
    -   **MVVM (Model-View-ViewModel)**: Used for the Home dashboard and data loading.
    -   **MVP (Model-View-Presenter)**: Utilized for game-specific logic and chessboard interactions.
-   **Asynchronous Programming**: [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) for efficient background data fetching.
-   **Android Jetpack**:
    -   **ViewModel & LiveData**: For lifecycle-aware data management.
    -   **Navigation**: Fragment-based navigation within a single Activity.
-   **UI Components**: Material Design components, Custom Views, and NestedScrollView.

## 📦 Project Structure

```text
com.denzo.daschess
├── data            # Repositories for data fetching
├── models          # Core data classes (MatchHistory, UserStats, ChessPuzzle)
├── customviews     # Specialized UI components like ChessboardView
├── HomeFragment    # Dashboard with stats and recent matches
├── GameFragment    # The main chessboard and game controls
└── MainActivity    # Navigation container
```

## 🏁 Getting Started

### Prerequisites

-   Android Studio (Latest stable version recommended)
-   JDK 1.8+
-   Android SDK 29+

### Installation

1.  Clone the repository:
    ```bash
    git clone https://github.com/denzo/daschess-kotlin.git
    ```
2.  Open the project in Android Studio.
3.  Sync the project with Gradle files.
4.  Run the application on an emulator or physical device.

---

*Enjoy the game of kings!*
