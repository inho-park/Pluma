package com.dowon.fluma.document.repo;

import com.dowon.fluma.document.domain.Document;
import com.dowon.fluma.document.repository.DocumentRepository;
import com.dowon.fluma.user.domain.User;
import com.dowon.fluma.user.repository.UserRepository;
import com.dowon.fluma.version.domain.Version;
import com.dowon.fluma.version.repository.VersionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DocumentRepositoryTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private VersionRepository versionRepository;

    /**
     * 시작 시 위에서 순서대로 실행하기
     */
    @Test
    public void 문서_생성() {
        User user;
        for (int i = 1; i < 11; i++) {
            user = userRepository.findByUsername("username" + i).orElseThrow();
            for (int j = 1; j < 11; j++) {
                documentRepository.save(
                        Document.builder()
                                .title("user" + i + "  title" + j)
                                .user(user)
                                .build()
                );
            }
        }
    }

    @Test
    public void 버전_생성() {
        Document document;
        for (long i = 1; i < 11; i++) {
            document = documentRepository.findById(i).orElseThrow();
            for (long j = 1; j < 11; j++) {
                    versionRepository.save(
                            Version.builder()
                                    .document(document)
                                    .content(
                                            document.getTitle() + " : " +
                                            "소설이라는 명칭은 원래 동양의 경우, 오늘날의 서사학적 개념과는 다소 다른 뜻을 가지고 있었다. 소설이라는 말이 처음으로 동양 기록에 나타난 것은 『장자(莊子)』 「외물편(外物篇)」과 『한서(漢書)』 「예문지(藝文志)」 등에서였다. 이들 기록을 보면, 소설이라는 말은 본래 대도(大道)와 거리가 먼 꾸민 말로서, 명예를 구하는 속된 말 나부랭이 또는 패(稗), 즉 세미(細米)와 같은 가담항어(街談巷語)주1의 뜻을 가지고 있었다. 즉, 패관(稗官)주2들에 의하여 채집되어 제왕이나 통치자의 참고자료가 되는 시정이나 길거리에서 얻어들은 말이나 이야기와 같은 소도(小道) 및 잔총소어(殘叢小語)의 뜻이다.\n" +
                                                    "\n" +
                                                    "우리 나라의 경우에도 소설이라는 명칭은 이규보(李奎報)의 『백운소설(白雲小說)』에서 처음 비롯되지만, 대개 패관문학(稗官文學) · 패설(稗說) · 패사(稗史) · 야승(野乘) · 수필 등의 포괄적이고 보잘것없는 속설로 인식되어 왔으며, 유학자들에 의해서 그 존재의미 자체가 긍정적이기보다는 부정적인 것으로 이해되어 왔다. 그러다가 개화기에 이르면서부터 량치차오(梁啓超)주3의 「논소설여군치지관계(論小說與群治之關係)」 등의 근대적 소설이론을 수용하면서 이러한 소설관에 변화가 두드러지게 일어나게 되고, 소설의 사회적인 효용성이 강조되었다.\n" +
                                                    "\n" +
                                                    "그러나 처음부터 그 평가가 부정적이기는 했지만 「외물편」이나 「예문지」에 나타나고 있는 설명은 소설의 원형으로서의 의미를 적지않게 암시하고 있다. 그것은 첫째, 소설에는 곧 허구(虛構)라는 개념이 내재되어 있다는 점이다. ‘꾸민다［飾］’나 ‘만든다［所造］’라는 말에 이미 허구의 가능성이 내재된 것도 사실이며 의사(擬似) 역사담론의 의미를 지닌다. 소설가의 원형이라고 할 수 있는 이른바 ‘패관’은 역사를 엄정한 사실에 근거하여 기록하는 사가(史家)인 태사공(太史公)주4에 비해서는 허구의 인간(Homofictor)이며, 사가라기보다는 일종의 작가인 것이다.\n" +
                                                    "\n" +
                                                    "이는 우리의 기록인 서거정(徐居正)의 『태평한화골계전(太平閑話滑稽傳)』에 밝힌 양성지(梁誠之)의 서문에, “패관소설은 유자(儒者)들이 문장을 가지고 우스개소리를 만들되, 넓은 지식을 펴기 위해서나 혹은 심심풀이를 하기 위하여 만든 것”이라는 설명에서도 볼 수 있다. 사가인 태사공은 사실의 주물숭배를 중시할 수밖에 없지만, 작가에 가까운 ‘패사씨’나 ‘외사씨’는 사실을 토대로 하면서도, 서사적으로 꾸미고 변형하고 윤색하게 되었던 것이다. 여기에서 실제로 일어난 것과 일어날 수 있는 것을 이용하는 역사와 소설의 상동성과 상이성이 연유된다. 역사는 역사성(historicity)과 사실성에 근거한 서사체(narrative)이며, 소설은 허구성(fictionality)에 근거한 서사체인 것이다. 요컨대, 사가인 사씨(史氏)나 태사씨(太史氏)와는 달리 패관은 소설가의 원형이다.\n" +
                                                    "\n" +
                                                    "둘째, 가담항어 또는 도청도설 및 잔총소어라는 의미는 경서(經書)나 사기(史記) 등에 비하여, 비록 세속적이고 천박하기는 할지라도 그것이 오히려 인간의 삶의 현장과 직결되어 있기 때문에, 현실성을 그만큼 존중한다는 의미를 지니고 있다는 점에서 오늘의 소설과 맥락이 이어져 있다. 소설이란 현실적인 삶을 재현하는 서사문학인 것이다.\n" +
                                                    "\n" +
                                                    "또한, 우리는 전통적으로 소설을 ‘이야기’라 일컫고 또 소설책을 ‘이야기책’이라고 일컫는다. 이런 인식 속에도 역시 두 개의 뜻이 내재되어 있는데, 서사성과 허구성이 곧 그것이다. 서사성이란 사건의 서술이라는 뜻이며, 허구성이란 사실의 전달과는 달리 상상력에 의하여 사실처럼 꾸민 것임을 뜻한다. 이야기란 사실의 재현일 수도 있지만, 흔히 ‘옛날 어느 곳에’라든가 ‘호랑이 담배필 적에’라는 허구적인 시간의 원점을 그 발단 부분에서 내세우고 있다. 이는 이야기 발단의 시간적 정식성인 동시에 허구화인 서사적 기본장치라고 할 수 있다. 이러한 시제장치를 전제로 한 이야기의 내용은 케테 함부르거(Hamburger, K.)가 일컫는 발언 주체의 자아원점(自我原點)과는 거리가 먼 꾸며진 말, 이른바 ‘비현실적 발언’이다. 또는 이른바 ‘미메시스(Mimesis, 모사)의 각도’가 넓은 서사양식이다.\n" +
                                                    "\n" +
                                                    "한편, 오늘날에 있어서 소설이라고 하면 흔히 영어의 ‘노블(novel)’을 연상한다. 이는 바로 중세의 서사문학인 ‘로맨스(romance)’에 대한 대립개념어이다. 중세문학인 로맨스는 보통 황당무계한 모험과 연대를 다루는 전기적(傳奇的) 이야기로서, 현실과 유리된 환상적인 귀족문학이다. 그리고 순결과 미덕을 보존하는 것에 가치를 두며 규범의 법칙을 존중한다. 이에 비하여 그에 대항하는 상업시민계층의 문학인 노블은 사회적인 탈을 쓴 현실적 인간의 성격과 사회적 현실과 사건을 냉철하게 관찰하고 이 양자를 결합하고 거기에서 하나의 세계상을 형성하려는 것이다.\n" +
                                                    "\n" +
                                                    "그러한 점에서 우리의 소설이라는 말의 개념 속에는 패관문학 · 이야기책 · 근대적인 노블 등을 포괄하기도 하지만, 다소 협의적으로 보면 현실의 삶을 대신하는 인물과 행동 및 인간관계가 약간의 복잡성을 띤 구성 속에서 극적으로 제시된 산문서술의 허구라는 의미를 가지고 있다. 따라서 그 개념을 요약하면 다음과 같다.\n" +
                                                    "\n" +
                                                    "첫째, 소설은 서사문학, 즉 이야기의 문학이기 때문에 극적으로 전개되는 구성적인 이야기이다.\n" +
                                                    "\n" +
                                                    "둘째, 소설의 이야기는 허구다. 작가는 실제의 인생에 대한 관찰에서 그 소재를 끌어 오지만, 그의 의도와 상상력에 따라 새롭게 선택하고 창조, 형성하기 때문에, 가공된 이야기이다.\n" +
                                                    "\n" +
                                                    "셋째, 소설의 이야기는 삶에 관련된 현실성을 가진다. 흔히 소설을 인간의 서사시라고 일컫는다.\n" +
                                                    "\n" +
                                                    "넷째, 소설은 서술의 문학이므로 서술자를 필수적으로 가진다.\n" +
                                                    "\n" +
                                                    "다섯째, 소설은 작가의 사상 · 인생관 · 사회관이 나타나는 문학양식이다.\n" +
                                                    "\n" +
                                                    "여섯째, 소설의 기원에 대해서 혹자는 서사체의 진보적 둥지에서 생겨난 한 가지로 보는 견해도 있고, 전대 서사문학과 단절시켜 보는 견해도 있다."
                                    )
                                    .build()
                    );
            }
        }
    }

    @Test
    public void 버전_확인() {
        System.out.print(versionRepository.findById(1l).orElseThrow().getContent());
    }
}
