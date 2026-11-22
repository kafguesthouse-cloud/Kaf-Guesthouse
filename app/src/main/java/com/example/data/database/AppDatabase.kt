package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.GuestHouseDao
import com.example.data.model.DepositStatus
import com.example.data.model.GuestEntity
import com.example.data.model.RoomEntity
import com.example.data.model.RoomStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [RoomEntity::class, GuestEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun guestHouseDao(): GuestHouseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kaf_halal_guest_house.db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialRoomsAndGuests(database.guestHouseDao())
                    }
                }
            }
        }

        suspend fun populateInitialRoomsAndGuests(dao: GuestHouseDao) {
            if (dao.getRoomCount() > 0) return

            val roomConfigs = listOf(
                // 1-4: Standard rooms
                RoomEntity(1, "ክፍል 01", "መደበኛ (Standard)", 1500.0, RoomStatus.VACANT),
                RoomEntity(2, "ክፍል 02", "መደበኛ (Standard)", 1500.0, RoomStatus.VACANT),
                RoomEntity(3, "ክፍል 03", "መደበኛ (Standard)", 1500.0, RoomStatus.VACANT),
                RoomEntity(4, "ክፍል 04", "መደበኛ (Standard)", 1500.0, RoomStatus.VACANT),

                // 5-8: Twin Bed rooms
                RoomEntity(5, "ክፍል 05", "ድርብ አልጋ (Twin)", 1800.0, RoomStatus.VACANT),
                RoomEntity(6, "ክፍል 06", "ድርብ አልጋ (Twin)", 1800.0, RoomStatus.VACANT),
                RoomEntity(7, "ክፍል 07", "ድርብ አልጋ (Twin)", 1800.0, RoomStatus.VACANT),
                RoomEntity(8, "ክፍል 08", "ድርብ አልጋ (Twin)", 1800.0, RoomStatus.VACANT),

                // 9-12: Deluxe rooms
                RoomEntity(9, "ክፍል 09", "ዴሉክስ (Deluxe)", 2200.0, RoomStatus.VACANT),
                RoomEntity(10, "ክፍል 10", "ዴሉክስ (Deluxe)", 2200.0, RoomStatus.VACANT),
                RoomEntity(11, "ክፍል 11", "ዴሉክስ (Deluxe)", 2200.0, RoomStatus.VACANT),
                RoomEntity(12, "ክፍል 12", "ዴሉክስ (Deluxe)", 2200.0, RoomStatus.VACANT),

                // 13-16: VIP / King rooms
                RoomEntity(13, "ክፍል 13", "ቪአይፒ (VIP Suite)", 3000.0, RoomStatus.VACANT),
                RoomEntity(14, "ክፍል 14", "ቪአይፒ (VIP Suite)", 3000.0, RoomStatus.VACANT),
                RoomEntity(15, "ክፍል 15", "ቪአይፒ (VIP Suite)", 3000.0, RoomStatus.VACANT),
                RoomEntity(16, "ክፍል 16", "ቪአይፒ (VIP Suite)", 3000.0, RoomStatus.VACANT)
            )

            dao.insertRooms(roomConfigs)

            val now = System.currentTimeMillis()
            val dayMillis = 24L * 60L * 60L * 1000L

            // Sample Guest 1: Checked in 2 days ago for 5 days (3 days remaining)
            val guest1 = GuestEntity(
                roomNumber = 2,
                fullName = "ሙሐመድ አብደላ",
                phoneNumber = "0911223344",
                idCardOrPassport = "ET-109482",
                checkInTimestamp = now - (2 * dayMillis) - (4 * 3600 * 1000L),
                stayDays = 5,
                expectedCheckOutTimestamp = now - (2 * dayMillis) + (3 * dayMillis),
                dailyRate = 1500.0,
                totalRentPaid = 7500.0,
                securityDeposit = 1000.0,
                depositStatus = DepositStatus.HELD,
                paymentMethod = "ቴሌብር (Telebirr)",
                notes = "መታወቂያ ኮፒ ተይዟል"
            )
            dao.checkInGuest(guest1)

            // Sample Guest 2: Checked in 3 days ago for 3 days (Expiring Today! 6 hours remaining)
            val guest2 = GuestEntity(
                roomNumber = 5,
                fullName = "ፋጢማ ሁሴን",
                phoneNumber = "0923456789",
                idCardOrPassport = "P-482019",
                checkInTimestamp = now - (3 * dayMillis) + (6 * 3600 * 1000L),
                stayDays = 3,
                expectedCheckOutTimestamp = now + (6 * 3600 * 1000L),
                dailyRate = 1800.0,
                totalRentPaid = 5400.0,
                securityDeposit = 1000.0,
                depositStatus = DepositStatus.HELD,
                paymentMethod = "ሲቢኢ ብር (CBE Birr)",
                notes = "ዛሬ ከቀትር በኋላ ይወጣሉ"
            )
            dao.checkInGuest(guest2)

            // Sample Guest 3: Deluxe room checked in yesterday for 7 days (6 days remaining)
            val guest3 = GuestEntity(
                roomNumber = 10,
                fullName = "አህመድ ኑሩ",
                phoneNumber = "0934567890",
                idCardOrPassport = "ET-887410",
                checkInTimestamp = now - (1 * dayMillis),
                stayDays = 7,
                expectedCheckOutTimestamp = now + (6 * dayMillis),
                dailyRate = 2200.0,
                totalRentPaid = 15400.0,
                securityDeposit = 2000.0,
                depositStatus = DepositStatus.HELD,
                paymentMethod = "በካሽ / Cash",
                notes = "ማስያዣ በጥሬ ገንዘብ"
            )
            dao.checkInGuest(guest3)

            // Sample Guest 4: VIP suite checked in 4 days ago for 4 days (Overdue by 3 hours)
            val guest4 = GuestEntity(
                roomNumber = 14,
                fullName = "ኡስማን ከድር",
                phoneNumber = "0945678901",
                idCardOrPassport = "P-901827",
                checkInTimestamp = now - (4 * dayMillis) - (3 * 3600 * 1000L),
                stayDays = 4,
                expectedCheckOutTimestamp = now - (3 * 3600 * 1000L),
                dailyRate = 3000.0,
                totalRentPaid = 12000.0,
                securityDeposit = 3000.0,
                depositStatus = DepositStatus.HELD,
                paymentMethod = "ባንክ ማስተላለፍ",
                notes = "ቀን እንደሚያራዝሙ ጠይቀዋል"
            )
            dao.checkInGuest(guest4)

            // Mark room 7 as cleaning
            dao.updateRoomStatus(7, RoomStatus.CLEANING)
        }
    }
}
