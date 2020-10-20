# Görev Optimizasyonu Web Servisi 

Bu web servis görev atamaları optimizasyonu için oluşturulmuştur. Görev ve Personel listesini excel dosyası yardımıyla içeri alabilir ve girilen günden başlayarak belirlenen gün uzunluğu tarihleri arasında bulunan tüm görevleri çözer/atama yapar.

Bu bir maven projesidir, spring boot kullanılmıştır. Optimizasyon çözücüsü olarak OptaPlanner kullanılmıştır. Test amacıyla kullanım için h2 in-memory veri tabanı kullanılmıştır.

## Problem.

Verilen görev ve personel listesini, çeşitli kriterleri göz önünde bulundurarak çözmek, görevlere atama yapmak amaçlanmıştır.

Görev ve Personel için içeri atarken kullanabileceğiniz excel şablonlarını aşağıdaki linklerde bulabilirsiniz.
 ([DutyList Şablon](https://github.com/cgrgrbz/dutyassignment/blob/master/src/main/resources/DutyList.xlsx)) 
 ([EmployeeList Şablon](https://github.com/cgrgrbz/dutyassignment/blob/master/src/main/resources/EmployeeList.xlsx)) 

**Proje yapısı (/src/main/java/com/cagrigurbuz/kayseriulasim/dutyassignment/):**
.
├── controller                          				# Controllers
├── domain                              				# Domain POJOs for the problem
├── repository                          				# JPA Repositories
├── service                             				# Implementations for the controllers
├── solver                              				# Solver related POJOs
├── utils                               				# IO Utils for import/export
├── DataImporter.java                   			  # Data Importer during the PostConstruct
├── DutyAssignmentApplication.java      
├── SwaggerConf.java                    		    # SwaggerUI configurations
└── README.md

## Problem Domain'i

![Class Diagram](https://raw.githubusercontent.com/cgrgrbz/dutyassignment/gh-pages/Class%20Diagram.png)

**:**
|Domain| açıklama |
|--|--|
| Görev | Bir görev, üzerinde karar değişkeni barındırır. |
| Personel | Görev(ler)e atanmak üzere bir personel. |
| Plan | Tüm görevleri içerisinde barındıran bir plan. |

Her görev üzerinde bir karar değişkeni, bir personel, bulunur ve çözüm sırasında atama yapılır.

Bir plan, sadece bir adet, çözüm sırasında oluşturulur. Görev listesi, başlangıç ve bitiş tarihi bulunur.

A Schedule, only and only one, instantiated during the solving time. It has a list of duties, a startDate, and a endDate. 
 

## Web Servis API Uç Noktaları


| kök | uç nokta | tip |  açıklama |
|--|--|--|--|
| duty | / | GET | Tüm görevlerin listesi |
| duty | /add | GET | Yeni görev ekle |
| duty | /import | POST | Excel dosyası kullanarak görevleri içeri aktar |
| duty | /current | GET | Sadece güncel planda bulunan görevlerin listesi |
| duty | /current/excel | GET | Güncel planda bulunan görev listelerini excel ile dışarı aktar |
| employee | / | GET | Tüm personellerin listesi |
| employee | /{employeeCode} | GET | Sicil numarası ile personel getir |
| employee | /add | POST | Yeni personel ekle |
| employee | /import | POST | Excel dosyası kullanarak personelleri içeri aktar |
| solver | /solve | POST | Planı çöz |
| solver | /terminate| POST | Çözücüyü sonlandır |

 

Uygulamanın localhost:8080 üzerinde çalıştığını varsayarak, 

- Swagger UI, api arayüz ekranı, [/swagger-ui.html](localhost:8080/swagger-ui.html)
- H2 Console, in-memort veritabanı [/h2-console](localhost:8080//h2-console)


## Nasıl Çalıştırılır?

Uygulama varsayılan olarak 8080 portu üzerinde çalışır ve Swagger UI ve H2 konsolu açıktır.

> **Projeyi IDEniz yardımıyla içeri aktarın ve çalıştırın**, veya maven komutlarını kullanarak konsol üzerinden,
> **_Swagger UI_ API arayüzüne gidin, veya kendi yöntemlerinizle POST ve GET uç birimlerini kullanarak devam edin,
> **Personel listesi excel dosyasını içeri aktarın _/employee/import_**
> **Görev listesi excel dosyasını içeri aktarın _/duty/import_**
>  **Çözücüye Başlangıç tarihi ve Plan Gün Uzunluğu parametreleri ile bir POST gönderin.**
>  **Daha önceden belirlenen (15dk) süre sonunda çözücü sonlandırılır ve, determined on [application.properties](https://github.com/cgrgrbz/dutyassignment/blob/master/src/main/resources/application.properties), görev uç birimlerini kullanarak güncel veya tüm görev listesini GET ile çağırıp, yapılan atamaları alabilirsiniz.**

Özel durumlarınızı/değişiklikleriniz için h2 veritabanını kullanabilirsiniz.

**YAPILACAKLR**
- 
- .

**HATALAR**
- .
- .

> by [CagriGurbuz](https://cagrigurbuz.com/).
