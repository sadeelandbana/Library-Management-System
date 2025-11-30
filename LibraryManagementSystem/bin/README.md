LibraryManagementSystem - Phase 1
================================

This project is prepared to fully match the Fall_2025_SE_Project_Library_System.pdf Phase 1 requirements:
- Admin login / logout
- Add/search books and CDs
- Borrow rules: books 28 days, CDs 7 days
- Overdue detection using Clock injection
- Fine calculation via Strategy pattern (10 NIS book, 20 NIS CD)
- Observer pattern for reminders (EmailNotifier / MockEmailNotifier)
- Unregister user restrictions
- JUnit tests (basic)

Open in Eclipse as a Maven project: File → Import → Existing Maven Projects → select this folder.
Run `mvn test` to execute tests.
Run the Main class: com.library.app.Main
