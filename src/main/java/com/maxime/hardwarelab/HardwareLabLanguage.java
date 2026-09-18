package com.maxime.hardwarelab;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public final class HardwareLabLanguage {
    public enum Language { ENGLISH, RUSSIAN }
    private static final Path CONFIG = FabricLoader.getInstance().getConfigDir().resolve("hardware_lab.json");
    private static Language language = Language.ENGLISH;

    private static final Map<String,String> EN = Map.ofEntries(
        Map.entry("block.universal_logic_gate","Universal Logic Gate"),
        Map.entry("block.digital_wire","Digital Wire"),
        Map.entry("block.redstone_input","Redstone Input"),
        Map.entry("block.redstone_output","Redstone Output"),
        Map.entry("block.clock_generator","Clock Generator"),
        Map.entry("block.clock_divider","Clock Divider"),
        Map.entry("block.d_flip_flop","D Flip-Flop"),
        Map.entry("block.digital_bus","Digital Bus"),
        Map.entry("block.bus_mux","Bus MUX"),
        Map.entry("block.bus_splitter","Bus Splitter"),
        Map.entry("block.bus_merger","Bus Merger"),
        Map.entry("block.bus_driver","Tri-State Bus Driver"),
        Map.entry("block.adc","4-bit ADC"),
        Map.entry("block.dac","4-bit DAC"),
        Map.entry("block.eight_bit_register","8-bit Register"),
        Map.entry("block.ram_256","RAM-256"),
        Map.entry("block.rom_256","ROM-256"),
        Map.entry("block.seven_segment_display","7-Segment Display"),
        Map.entry("block.led_matrix","8x8 LED Matrix"),
        Map.entry("block.cpu","8-bit CPU"),
        Map.entry("block.fpga","FPGA 4-LUT"),
        Map.entry("item.logic_probe","Logic Probe"),
        Map.entry("item.oscilloscope","Oscilloscope"),
        Map.entry("control.universal_logic_gate","Right-click: next logic function"),
        Map.entry("control.digital_wire","Place next to hardware; connects automatically"),
        Map.entry("control.redstone_input","Marked face = redstone input"),
        Map.entry("control.redstone_output","Marked face = redstone output"),
        Map.entry("control.clock_generator","Right-click: change clock rate"),
        Map.entry("control.clock_divider","Right-click: change division"),
        Map.entry("control.d_flip_flop","D + CLK in, Q out"),
        Map.entry("control.digital_bus","Right-click: pattern | Shift + right-click: width"),
        Map.entry("control.bus_mux","SELECT = redstone | right-click: width"),
        Map.entry("control.bus_splitter","Right-click: bank | Shift + right-click: width"),
        Map.entry("control.bus_merger","Right-click: bank | Shift + right-click: width"),
        Map.entry("control.bus_driver","Side redstone input = enable"),
        Map.entry("control.adc","Redstone power -> 4-bit bus"),
        Map.entry("control.dac","4-bit bus -> redstone power"),
        Map.entry("control.eight_bit_register","Clock/load controls the latch"),
        Map.entry("control.ram_256","Address selects byte | Shift + right-click: clear"),
        Map.entry("control.rom_256","Right-click: cycle demo pattern"),
        Map.entry("control.seven_segment_display","Feed a 4-bit hexadecimal value"),
        Map.entry("control.led_matrix","Feed row select + 8-bit data"),
        Map.entry("control.cpu","Right-click: program | Shift + right-click: speed"),
        Map.entry("control.fpga","Right-click: LUT mode | Shift + right-click: register"),
        Map.entry("control.logic_probe","Right-click a component: live details"),
        Map.entry("control.oscilloscope","Right-click a source: live waveform"),
        Map.entry("gui.guide.title","Hardware Lab Guide"),
        Map.entry("gui.guide.header","HARDWARE LAB // QUICK START"),
        Map.entry("gui.guide.page","PAGE"),
        Map.entry("gui.guide.step1","1  PLACE A REDSTONE INPUT"),
        Map.entry("gui.guide.step1b","Put a lever behind it; the marked face is the digital output"),
        Map.entry("gui.guide.step2","2  RUN A DIGITAL WIRE"),
        Map.entry("gui.guide.step2b","Place Digital Wire between the source and the receiver"),
        Map.entry("gui.guide.step3","3  BUILD A GATE"),
        Map.entry("gui.guide.step3b","Place a Universal Logic Gate; right-click cycles its function"),
        Map.entry("gui.guide.step4","4  INSPECT THE SIGNAL"),
        Map.entry("gui.guide.step4b","Logic Probe = details, Oscilloscope = waveform"),
        Map.entry("gui.guide.rules","SIGNAL RULES"),
        Map.entry("gui.guide.rule1","HIGH / green = digital 1"),
        Map.entry("gui.guide.rule2","LOW / gray = digital 0"),
        Map.entry("gui.guide.rule3","Ports are physical faces; direction matters"),
        Map.entry("gui.guide.rule4","Shift + right-click changes configurable blocks"),
        Map.entry("gui.guide.logic","LOGIC & TIMING"),
        Map.entry("gui.guide.buses","BUSES, MEMORY & COMPUTE"),
        Map.entry("gui.guide.tools","TOOLS"),
        Map.entry("gui.guide.settings","SETTINGS"),
        Map.entry("gui.guide.footer","H  GUIDE     LEFT / RIGHT  PAGE     ESC  CLOSE"),
        Map.entry("gui.settings.title","Hardware Lab Settings"),
        Map.entry("gui.settings.language","MOD LANGUAGE"),
        Map.entry("gui.settings.language_help","Changes Hardware Lab screens, names and tooltips"),
        Map.entry("gui.settings.english","English"),
        Map.entry("gui.settings.russian","Russian"),
        Map.entry("gui.settings.selected","SELECTED"),
        Map.entry("gui.settings.back","Back"),
        Map.entry("gui.info.title","Hardware Lab Component"),
        Map.entry("gui.info.face","CLICKED FACE"),
        Map.entry("gui.info.live","LIVE SIGNAL"),
        Map.entry("gui.info.control","CONTROL"),
        Map.entry("gui.info.hint","Use marked faces as ports. Digital Wire connects automatically."),
        Map.entry("gui.info.guide","H  GUIDE"),
        Map.entry("gui.info.close","ESC  CLOSE"),
        Map.entry("gui.scope.title","HARDWARE LAB // OSCILLOSCOPE"),
        Map.entry("gui.scope.source","SOURCE"),
        Map.entry("gui.scope.face","FACE"),
        Map.entry("gui.scope.live","LIVE SIGNAL"),
        Map.entry("gui.scope.high","HIGH"),
        Map.entry("gui.scope.low","LOW"),
        Map.entry("gui.scope.power","POWER"),
        Map.entry("gui.scope.reset","R  RESET"),
        Map.entry("gui.scope.close","ESC  CLOSE"),
        Map.entry("gui.tooltip.header","Hardware Lab"),
        Map.entry("gui.tooltip.guide","H = Hardware Guide")
    );

    private static final Map<String,String> RU = Map.ofEntries(
        Map.entry("block.universal_logic_gate","Универсальный логический элемент"),
        Map.entry("block.digital_wire","Цифровой провод"),
        Map.entry("block.redstone_input","Вход редстоуна"),
        Map.entry("block.redstone_output","Выход редстоуна"),
        Map.entry("block.clock_generator","Генератор тактового сигнала"),
        Map.entry("block.clock_divider","Делитель частоты"),
        Map.entry("block.d_flip_flop","D-триггер"),
        Map.entry("block.digital_bus","Цифровая шина"),
        Map.entry("block.bus_mux","Мультиплексор шины"),
        Map.entry("block.bus_splitter","Разветвитель шины"),
        Map.entry("block.bus_merger","Объединитель шины"),
        Map.entry("block.bus_driver","Тристабильный драйвер шины"),
        Map.entry("block.adc","4-битный АЦП"),
        Map.entry("block.dac","4-битный ЦАП"),
        Map.entry("block.eight_bit_register","8-битный регистр"),
        Map.entry("block.ram_256","ОЗУ-256"),
        Map.entry("block.rom_256","ПЗУ-256"),
        Map.entry("block.seven_segment_display","7-сегментный индикатор"),
        Map.entry("block.led_matrix","LED-матрица 8x8"),
        Map.entry("block.cpu","8-битный процессор"),
        Map.entry("block.fpga","FPGA 4-LUT"),
        Map.entry("item.logic_probe","Логический пробник"),
        Map.entry("item.oscilloscope","Осциллограф"),
        Map.entry("control.universal_logic_gate","ПКМ: сменить логическую функцию"),
        Map.entry("control.digital_wire","Ставь рядом с аппаратными блоками — соединится сам"),
        Map.entry("control.redstone_input","Метка = вход редстоуна"),
        Map.entry("control.redstone_output","Метка = выход редстоуна"),
        Map.entry("control.clock_generator","ПКМ: сменить частоту"),
        Map.entry("control.clock_divider","ПКМ: сменить деление"),
        Map.entry("control.d_flip_flop","D + CLK на входе, Q на выходе"),
        Map.entry("control.digital_bus","ПКМ: шаблон | Shift + ПКМ: ширина"),
        Map.entry("control.bus_mux","SELECT = редстоун | ПКМ: ширина"),
        Map.entry("control.bus_splitter","ПКМ: банк | Shift + ПКМ: ширина"),
        Map.entry("control.bus_merger","ПКМ: банк | Shift + ПКМ: ширина"),
        Map.entry("control.bus_driver","Боковой редстоун = разрешение"),
        Map.entry("control.adc","Сила редстоуна -> 4-битная шина"),
        Map.entry("control.dac","4-битная шина -> редстоун"),
        Map.entry("control.eight_bit_register","Такт/загрузка управляют защёлкой"),
        Map.entry("control.ram_256","Адрес выбирает байт | Shift + ПКМ: очистка"),
        Map.entry("control.rom_256","ПКМ: сменить демо-шаблон"),
        Map.entry("control.seven_segment_display","Подай 4-битное HEX-значение"),
        Map.entry("control.led_matrix","Подай выбор строки + 8-битные данные"),
        Map.entry("control.cpu","ПКМ: программа | Shift + ПКМ: скорость"),
        Map.entry("control.fpga","ПКМ: режим LUT | Shift + ПКМ: регистр"),
        Map.entry("control.logic_probe","ПКМ по блоку: состояние и детали"),
        Map.entry("control.oscilloscope","ПКМ по источнику: сигнал во времени"),
        Map.entry("gui.guide.title","Справочник Hardware Lab"),
        Map.entry("gui.guide.header","HARDWARE LAB // БЫСТРЫЙ СТАРТ"),
        Map.entry("gui.guide.page","СТРАНИЦА"),
        Map.entry("gui.guide.step1","1  ПОСТАВЬ ВХОД РЕДСТОУНА"),
        Map.entry("gui.guide.step1b","Поставь рычаг сзади; отмеченная сторона — цифровой выход"),
        Map.entry("gui.guide.step2","2  ПРОВЕДИ ЦИФРОВОЙ ПРОВОД"),
        Map.entry("gui.guide.step2b","Поставь Digital Wire между источником и приёмником"),
        Map.entry("gui.guide.step3","3  СОБЕРИ ЛОГИКУ"),
        Map.entry("gui.guide.step3b","Поставь логический элемент; ПКМ переключает его функцию"),
        Map.entry("gui.guide.step4","4  ПРОВЕРЬ СИГНАЛ"),
        Map.entry("gui.guide.step4b","Logic Probe показывает детали, Oscilloscope — форму сигнала"),
        Map.entry("gui.guide.rules","ПРАВИЛА СИГНАЛА"),
        Map.entry("gui.guide.rule1","HIGH / зелёный = цифровая 1"),
        Map.entry("gui.guide.rule2","LOW / серый = цифровой 0"),
        Map.entry("gui.guide.rule3","Порты — физические стороны блока, направление важно"),
        Map.entry("gui.guide.rule4","Shift + ПКМ меняет настройки настраиваемых блоков"),
        Map.entry("gui.guide.logic","ЛОГИКА И ТАКТИРОВАНИЕ"),
        Map.entry("gui.guide.buses","ШИНЫ, ПАМЯТЬ И ВЫЧИСЛЕНИЯ"),
        Map.entry("gui.guide.tools","ИНСТРУМЕНТЫ"),
        Map.entry("gui.guide.settings","НАСТРОЙКИ"),
        Map.entry("gui.guide.footer","H  СПРАВКА     ВЛЕВО / ВПРАВО  СТРАНИЦА     ESC  ЗАКРЫТЬ"),
        Map.entry("gui.settings.title","Настройки Hardware Lab"),
        Map.entry("gui.settings.language","ЯЗЫК МОДА"),
        Map.entry("gui.settings.language_help","Меняет язык экранов, названий и подсказок Hardware Lab"),
        Map.entry("gui.settings.english","Английский"),
        Map.entry("gui.settings.russian","Русский"),
        Map.entry("gui.settings.selected","ВЫБРАН"),
        Map.entry("gui.settings.back","Назад"),
        Map.entry("gui.info.title","Компонент Hardware Lab"),
        Map.entry("gui.info.face","НАЖАТАЯ СТОРОНА"),
        Map.entry("gui.info.live","ТЕКУЩИЙ СИГНАЛ"),
        Map.entry("gui.info.control","УПРАВЛЕНИЕ"),
        Map.entry("gui.info.hint","Используй отмеченные стороны как порты. Digital Wire соединяется автоматически."),
        Map.entry("gui.info.guide","H  СПРАВКА"),
        Map.entry("gui.info.close","ESC  ЗАКРЫТЬ"),
        Map.entry("gui.scope.title","HARDWARE LAB // ОСЦИЛЛОГРАФ"),
        Map.entry("gui.scope.source","ИСТОЧНИК"),
        Map.entry("gui.scope.face","СТОРОНА"),
        Map.entry("gui.scope.live","ТЕКУЩИЙ СИГНАЛ"),
        Map.entry("gui.scope.high","HIGH"),
        Map.entry("gui.scope.low","LOW"),
        Map.entry("gui.scope.power","СИЛА"),
        Map.entry("gui.scope.reset","R  СБРОС"),
        Map.entry("gui.scope.close","ESC  ЗАКРЫТЬ"),
        Map.entry("gui.tooltip.header","Hardware Lab"),
        Map.entry("gui.tooltip.guide","H = Справочник Hardware Lab")
    );

    static { load(); }
    private HardwareLabLanguage() {}

    public static String text(String key) { return (language == Language.RUSSIAN ? RU : EN).getOrDefault(key, key); }
    public static Component component(String key) { return Component.literal(text(key)); }
    public static String blockName(String path) { return text("block." + path); }
    public static String itemName(String path) { return text("item." + path); }
    public static String control(String path) { return text("control." + path); }
    public static boolean isRussian() { return language == Language.RUSSIAN; }
    public static void setLanguage(Language next) { language = next; save(); }

    private static void load() {
        try {
            if (Files.exists(CONFIG) && Files.readString(CONFIG).contains(""language":"ru"")) {
                language = Language.RUSSIAN;
            }
        } catch (IOException ignored) {}
    }

    private static void save() {
        try {
            Files.createDirectories(CONFIG.getParent());
            Files.writeString(CONFIG, "{
  "language": "" + (language == Language.RUSSIAN ? "ru" : "en") + ""
}
");
        } catch (IOException ignored) {}
    }
}
