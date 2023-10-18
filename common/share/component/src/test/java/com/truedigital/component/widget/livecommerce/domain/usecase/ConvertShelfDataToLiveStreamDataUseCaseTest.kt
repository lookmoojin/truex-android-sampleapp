package com.truedigital.component.widget.livecommerce.domain.usecase

import com.google.gson.Gson
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.test.assertEquals

class ConvertShelfDataToLiveStreamDataUseCaseTest {

    private lateinit var convertShelfDataToLiveStreamDataUseCase: ConvertShelfDataToLiveStreamDataUseCase

    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()

    @BeforeEach
    fun setup() {
        convertShelfDataToLiveStreamDataUseCase = ConvertShelfDataToLiveStreamDataUseCaseImpl(
            Gson()
        )
    }

    @Test
    fun `Put the data into function will get presentation model`() {
        val result = convertShelfDataToLiveStreamDataUseCase.execute(
            "{\n" +
                "                \"display_country\": \"th\",\n" +
                "                \"display_lang\": \"th\",\n" +
                "                \"id\": \"WLM6Q03Ojyzg\",\n" +
                "                \"content_type\": \"misc\",\n" +
                "                \"original_id\": null,\n" +
                "                \"title\": \"Commerce : livestream-component\",\n" +
                "                \"article_category\": null,\n" +
                "                \"thumb\": \"\",\n" +
                "                \"tags\": null,\n" +
                "                \"status\": \"publish\",\n" +
                "                \"count_views\": null,\n" +
                "                \"publish_date\": \"2022-07-25T15:50:45.000Z\",\n" +
                "                \"create_date\": \"2022-07-25T15:53:12.491Z\",\n" +
                "                \"update_date\": \"2022-10-17T05:02:51.008Z\",\n" +
                "                \"searchable\": \"N\",\n" +
                "                \"create_by\": \"DechaA\",\n" +
                "                \"create_by_ssoid\": \"58082955\",\n" +
                "                \"update_by\": \"TookTik\",\n" +
                "                \"update_by_ssoid\": \"55842695\",\n" +
                "                \"source_url\": null,\n" +
                "                \"count_likes\": null,\n" +
                "                \"count_ratings\": null,\n" +
                "                \"source_country\": \"th\",\n" +
                "                \"setting\": {\n" +
                "                    \"component_name\": \"commerce-livestream\",\n" +
                "                    \"navigate\": \"\",\n" +
                "                    \"ssoid_list\": \"21968216,183080,21979746,22003207,21990796,22001350,22000665,22005846\",\n" +
                "                    \"title\": \"Live Shopping ไทย\"\n" +
                "                }\n" +
                "            }"
        )

        assertEquals("Live Shopping ไทย", result.first)
        assertEquals("", result.second)
        assertEquals(
            "21968216,183080,21979746,22003207,21990796,22001350,22000665,22005846",
            result.third
        )
    }

    @Test
    fun `Put the empty data into function will get empty presentation model`() {
        val result = convertShelfDataToLiveStreamDataUseCase.execute(
            "{\n" +
                "            }"
        )

        assertEquals("", result.first)
        assertEquals("", result.second)
        assertEquals(
            "",
            result.third
        )
    }
}
