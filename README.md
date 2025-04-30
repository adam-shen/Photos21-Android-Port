# Photos21-Android-Port

This is a port of [Photos JavaFX project](https://github.com/adam-shen/Photos21)

## 🧪 Key Features Implemented

- 📂 Create, delete, rename albums (long press on album to see options)
- 🖼️ Add/remove photos from albums using gallery picker (Google Photos supported)
- 📸 View photos in slideshow with next/prev controls
- 🏷️ Add/delete tags per photo (only `person` and `location` tags allowed)
- 🔀 Move photos between albums (available in slideshow view)
- 🔍 Search photos by tags (autocomplete + AND/OR logic, searches across all albums)

---

## ⚙️ Usage Tips

- **Long Press** on any album in the home screen to rename or delete it
- **Click** on a photo inside an album to view it in a slideshow
- **From the slideshow**, you can:
  - Add tags
  - Delete existing tags
  - Move the photo to another album
- Use the **Search** button on the main screen to search across all albums by tag

---

## 💾 Data Persistence

All album and photo data is automatically saved between sessions using Java serialization (`albums.ser` in internal storage).

---

## 🚨 API Compatibility

- Target SDK: 34
- Min SDK: 34  
- Emulator tested on: Pixel 6 (1080 x 2400, 420dpi)  
- Ensure your emulator/device supports image picking via `ACTION_OPEN_DOCUMENT`

---

## 🤖 GenAI Usage (Required)

This project used GenAI (ChatGPT) as an assistant for:
- Converting JavaFX architecture into Android architecture
- Setting up RecyclerViews and Adapters
- Writing UI XML layouts (activity_album, activity_slideshow, activity_search)
- Implementing complex tag search logic with AND/OR and autocomplete
- Refactoring logic for slideshow navigation and photo management
- Providing debugging tips for Gradle, lint errors, and emulator integration

All code was reviewed and integrated manually, one feature at a time, in accordance with assignment guidelines. No end-to-end code was generated in a single prompt.

---

## 📁 Project Structure Highlights

- `MainActivity.java`: Entry screen for managing albums
- `AlbumActivity.java`: Displays all photos in a selected album
- `SlideshowActivity.java`: Full-screen photo viewer and editor
- `SearchActivity.java`: Handles tag-based search
- `SerializationUtil.java`: Saves/loads persistent data
- `PhotoManager.java`, `SearchResultAdapter.java`: RecyclerView adapters
