Εκπαιδευτικη εκδοση εφαρμογης

Αυτος ο φακελος περιεχει μια απλουστερη εκδοση της εφαρμογης σε ενα μονο αρχειο:

EducationalMedicalCenter.java

Σκοπος

Η εκδοση αυτη δεν αντικαθιστα απαραιτητα την κανονικη εργασια. Υπαρχει για να ειναι πιο ευκολη η κατανοηση και η παρουσιαση της λογικης:

- ολες οι βασικες λιστες βρισκονται στην αρχη του αρχειου
- το main menu φαινεται καθαρα στη main
- οι μεθοδοι add/list/statistics ειναι απλες και σειριακες
- οι κλασεις Doctor, Patient, Exam και Appointment ειναι στο ιδιο αρχειο
- η κληρονομικοτητα φαινεται με την abstract Exam και τις τρεις subclasses
- η αποθηκευση γινεται σε απλα text files με prefix simple_

Τι κρατηθηκε απο την αρχικη εφαρμογη

- Γιατροι
- Ασθενεις
- Εξετασεις
- Ραντεβου
- Φορτωση και αποθηκευση σε αρχεια
- Υπολογισμος κοστους με fast results
- Ελεγχος οτι δεν ξεπερνιεται το maxSlotsPerDay
- Στατιστικα εσοδων ανα ασθενη και ανα εξεταση

Τι απλοποιηθηκε

- Δεν υπαρχει ξεχωριστο DataStore.
- Δεν υπαρχει interface MedicalCenterStore.
- Δεν υπαρχει Snapshot object.
- Δεν υπαρχει ξεχωριστη κλαση InputHelper.
- Δεν υπαρχει εκτεταμενο validation ημερομηνιων.
- Η αποθηκευση CSV ειναι πιο απλη και δεν κανει escaping για ειδικους χαρακτηρες.

Μεταγλωττιση

Απο τον φακελο educational_version:

javac EducationalMedicalCenter.java

Στο συγκεκριμενο περιβαλλον:

/home/krimits/jdk-11.0.2/bin/javac EducationalMedicalCenter.java

Εκτελεση

java EducationalMedicalCenter

Στο συγκεκριμενο περιβαλλον:

/home/krimits/jdk-11.0.2/bin/java EducationalMedicalCenter

Πως να την παρουσιασεις

1. Ξεκινα απο τη main και δειξε οτι φορτωνει δεδομενα, εμφανιζει menu και στο τελος αποθηκευει.
2. Δειξε τις ArrayList doctors, patients, exams και appointments.
3. Εξηγησε οτι τα ραντεβου συνδεουν patientId και examId.
4. Δειξε την abstract Exam και μετα μια subclass, π.χ. ImagingExam.
5. Εξηγησε οτι το getCost αλλαζει αναλογα με fastResults και τον τυπο εξετασης.
6. Δειξε τα saveData/loadData ως απλη αποθηκευση σε text files.
