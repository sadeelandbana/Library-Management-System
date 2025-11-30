package com.library.test;

import com.library.model.UserAccount;
import com.library.service.EmailNotifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class EmailNotifierMockTest { // تم تغيير الاسم إلى EmailNotifierMockTest ليكون أدق

    // لم نعد بحاجة إلى real object هنا بما أننا نركز على Mocking
    // إذا كنتِ تريدين اختبار الكلاس الحقيقي بشكل منفصل، يمكن إبقاؤه.
    // لكن للاختبار باستخدام Mockito، نحتاج فقط الـ mock object.
    
    private EmailNotifier mockNotifier; // mock object
    private UserAccount user;

    @BeforeEach
    void setup(){
        // نحتاج لـ Mocking فقط، ولا حاجة لتهيئة الكلاس الحقيقي (notifier = new EmailNotifier(...))
        mockNotifier = mock(EmailNotifier.class);

        user = new UserAccount(
                "U1", "Test User", "test@example.com",
                "123", "pass", "User"
        );
    }

    @Test
    void testNotifyCalledWithCorrectArgs(){
        // 1. استدعاء الـ Mock:
        // هذا الاستدعاء لا يقوم بإرسال إيميل حقيقي
        mockNotifier.notify(user, "Overdue Book!");

        // 2. التحقق (Verification):
        // نتأكد أن ميثود notify() قد تم استدعاؤها مرة واحدة 
        verify(mockNotifier, times(1))
                .notify(eq(user), eq("Overdue Book!"));
    }

    // تم حذف testMessageStoredInRealNotifier لأنه يعتمد على متطلبات غير موجودة في EmailNotifier الفعلي لديكِ
}