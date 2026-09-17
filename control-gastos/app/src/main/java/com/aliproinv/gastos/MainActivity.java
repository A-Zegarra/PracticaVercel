package com.aliproinv.gastos;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
  static final int TEXT=Color.rgb(15,23,42), MUTED=Color.rgb(100,116,139), ACCENT=Color.rgb(79,70,229),
      ACCENT2=Color.rgb(15,118,110), GREEN=Color.rgb(5,150,105), RED=Color.rgb(220,38,38),
      WHITE=Color.WHITE;
  DB db; LinearLayout body,nav; int tab=0;
  final NumberFormat money=NumberFormat.getCurrencyInstance(new Locale("es","PE"));

  int dp(int x){return (int)(x*getResources().getDisplayMetrics().density+.5f);}
  TextView t(String s,int z,int c,boolean b){TextView v=new TextView(this);v.setText(s);v.setTextSize(z);v.setTextColor(c);if(b)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return v;}
  GradientDrawable shape(int fill,int radius){GradientDrawable g=new GradientDrawable();g.setColor(fill);g.setCornerRadius(dp(radius));return g;}
  GradientDrawable outlined(int fill,int radius,int stroke){GradientDrawable g=shape(fill,radius);g.setStroke(dp(1),stroke);return g;}
  GradientDrawable glassShape(){return outlined(0xD9FFFFFF,22,0xAAFFFFFF);}
  GradientDrawable bgGradient(){return new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{Color.rgb(238,242,255),Color.rgb(240,253,250),Color.rgb(248,250,252)});}
  GradientDrawable heroGradient(){GradientDrawable g=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{Color.rgb(67,56,202),Color.rgb(79,70,229),Color.rgb(15,118,110)});g.setCornerRadius(dp(28));return g;}

  LinearLayout card(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);l.setPadding(dp(17),dp(17),dp(17),dp(17));l.setBackground(glassShape());l.setElevation(dp(2));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,0,0,dp(12));l.setLayoutParams(p);return l;}
  MaterialButton btn(String s){MaterialButton b=new MaterialButton(this);b.setText(s);b.setAllCaps(false);b.setTextColor(WHITE);b.setTextSize(15);b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);b.setCornerRadius(dp(16));b.setBackgroundTintList(ColorStateList.valueOf(ACCENT));b.setMinHeight(dp(52));b.setInsetTop(0);b.setInsetBottom(0);return b;}
  EditText input(String hint){EditText e=new EditText(this);e.setHint(hint);e.setTextSize(16);e.setTextColor(TEXT);e.setHintTextColor(MUTED);e.setSingleLine();e.setPadding(dp(14),0,dp(14),0);e.setBackground(outlined(0xBFFFFFFF,14,0x55CBD5E1));return e;}
  FrameLayout iconBox(String key,int fg,int bg,int size){FrameLayout f=new FrameLayout(this);f.setBackground(shape(bg,16));IconView iv=new IconView(this,key,fg);FrameLayout.LayoutParams ip=new FrameLayout.LayoutParams(dp(size/2),dp(size/2),Gravity.CENTER);f.addView(iv,ip);return f;}

  @Override public void onCreate(Bundle b){
    super.onCreate(b);db=new DB(this);
    getWindow().setStatusBarColor(Color.rgb(238,242,255));
    getWindow().setNavigationBarColor(Color.rgb(248,250,252));
    shell();dashboard();
  }

  void shell(){
    LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackground(bgGradient());
    body=new LinearLayout(this);body.setOrientation(LinearLayout.VERTICAL);
    ScrollView sv=new ScrollView(this);sv.setFillViewport(true);sv.setClipToPadding(false);sv.addView(body);
    root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));
    nav=new LinearLayout(this);nav.setOrientation(LinearLayout.HORIZONTAL);nav.setGravity(Gravity.CENTER);
    nav.setPadding(dp(8),dp(7),dp(8),dp(8));nav.setBackground(outlined(0xEFFFFFFF,0,0x55FFFFFF));nav.setElevation(dp(8));
    root.addView(nav,new LinearLayout.LayoutParams(-1,dp(72)));setContentView(root);nav();
  }

  void nav(){
    nav.removeAllViews();
    String[] labels={"Inicio","Movimientos","Categorías"};
    String[] icons={"home","swap","category"};
    for(int i=0;i<3;i++){
      final int x=i;boolean active=i==tab;
      LinearLayout cell=new LinearLayout(this);cell.setOrientation(LinearLayout.VERTICAL);cell.setGravity(Gravity.CENTER);
      cell.setPadding(dp(4),dp(4),dp(4),dp(4));
      if(active)cell.setBackground(shape(0x194F46E5,16));
      FrameLayout ib=iconBox(icons[i],active?ACCENT:MUTED,Color.TRANSPARENT,34);
      cell.addView(ib,new LinearLayout.LayoutParams(dp(34),dp(30)));
      TextView label=t(labels[i],11,active?ACCENT:MUTED,active);label.setGravity(Gravity.CENTER);
      cell.addView(label,new LinearLayout.LayoutParams(-1,dp(24)));
      cell.setOnClickListener(v->{tab=x;nav();if(x==0)dashboard();else if(x==1)movements();else categories();});
      LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-1,1);p.setMargins(dp(3),0,dp(3),0);nav.addView(cell,p);
    }
  }

  void page(String title,String sub){
    body.removeAllViews();body.setPadding(dp(16),dp(16),dp(16),dp(28));
    LinearLayout brand=new LinearLayout(this);brand.setGravity(Gravity.CENTER_VERTICAL);
    brand.addView(iconBox("wallet",WHITE,ACCENT,42),new LinearLayout.LayoutParams(dp(42),dp(42)));
    TextView name=t("MiSaldo",19,TEXT,true);name.setPadding(dp(10),0,0,0);brand.addView(name);
    body.addView(brand,new LinearLayout.LayoutParams(-1,dp(48)));
    TextView h=t(title,28,TEXT,true);h.setPadding(0,dp(10),0,0);body.addView(h);
    TextView s=t(sub,14,MUTED,false);s.setPadding(0,dp(4),0,dp(18));body.addView(s);
  }

  void dashboard(){
    tab=0;nav();page("Resumen","Tu dinero, claro y simple");
    Totals x=db.totals();

    LinearLayout hero=new LinearLayout(this);hero.setOrientation(LinearLayout.VERTICAL);hero.setPadding(dp(20),dp(20),dp(20),dp(20));hero.setBackground(heroGradient());hero.setElevation(dp(5));
    LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);
    top.addView(iconBox("wallet",WHITE,0x22FFFFFF,42),new LinearLayout.LayoutParams(dp(42),dp(42)));
    TextView st=t("Saldo disponible",14,0xFFE0E7FF,false);st.setPadding(dp(10),0,0,0);top.addView(st);hero.addView(top);
    TextView val=t(money.format(x.in-x.out),34,WHITE,true);val.setPadding(0,dp(14),0,dp(2));hero.addView(val);
    TextView foot=t("Entradas menos salidas acumuladas",12,0xFFDDE7FF,false);hero.addView(foot);
    LinearLayout.LayoutParams hp=new LinearLayout.LayoutParams(-1,-2);hp.setMargins(0,0,0,dp(12));body.addView(hero,hp);

    LinearLayout row=new LinearLayout(this);
    LinearLayout ci=small("Entradas",money.format(x.in),GREEN,"up"),co=small("Salidas",money.format(x.out),RED,"down");
    LinearLayout.LayoutParams pi=new LinearLayout.LayoutParams(0,-2,1);pi.setMargins(0,0,dp(6),dp(12));row.addView(ci,pi);
    LinearLayout.LayoutParams po=new LinearLayout.LayoutParams(0,-2,1);po.setMargins(dp(6),0,0,dp(12));row.addView(co,po);body.addView(row);

    MaterialButton add=btn("Registrar movimiento");add.setOnClickListener(v->movementDialog());
    LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(-1,dp(54));ap.setMargins(0,0,0,dp(16));body.addView(add,ap);

    LinearLayout graph=card();graph.addView(sectionTitle("Entradas vs. salidas","Acumulado general"));
    graph.addView(new Bars(this,x.in,x.out),new LinearLayout.LayoutParams(-1,dp(178)));body.addView(graph);

    LinearLayout pc=card();pc.addView(sectionTitle("Por categoría","Dónde entra y sale tu dinero"));
    ArrayList<CT> list=db.categoryTotals();
    if(list.isEmpty())pc.addView(empty("Aún no hay datos por categoría."));
    else pc.addView(new CatBars(this,list),new LinearLayout.LayoutParams(-1,dp(Math.max(130,list.size()*72))));
    body.addView(pc);

    LinearLayout recent=card();recent.addView(sectionTitle("Últimos movimientos","Tus registros más recientes"));
    ArrayList<Move> ms=db.moves(5);
    if(ms.isEmpty())recent.addView(empty("Aún no registraste movimientos."));
    else for(Move m:ms)recent.addView(moveRow(m));
    body.addView(recent);
  }

  View sectionTitle(String a,String b){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);l.addView(t(a,18,TEXT,true));TextView s=t(b,12,MUTED,false);s.setPadding(0,dp(2),0,dp(8));l.addView(s);return l;}
  TextView empty(String s){TextView e=t(s,14,MUTED,false);e.setPadding(0,dp(12),0,dp(6));return e;}

  LinearLayout small(String a,String b,int c,String icon){
    LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);l.setPadding(dp(14),dp(14),dp(14),dp(14));l.setBackground(glassShape());l.setElevation(dp(2));
    LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);top.addView(iconBox(icon,c,0x0D000000,30),new LinearLayout.LayoutParams(dp(30),dp(30)));
    TextView lab=t(a,13,MUTED,false);lab.setPadding(dp(7),0,0,0);top.addView(lab);l.addView(top);
    TextView v=t(b,18,c,true);v.setPadding(0,dp(9),0,0);l.addView(v);return l;
  }

  void movements(){
    tab=1;nav();page("Movimientos","Historial de entradas y salidas");
    MaterialButton add=btn("Nuevo movimiento");add.setOnClickListener(v->movementDialog());
    LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(54));p.setMargins(0,0,0,dp(16));body.addView(add,p);
    ArrayList<Move> ms=db.moves(500);
    if(ms.isEmpty()){LinearLayout c=card();c.addView(empty("No hay movimientos registrados."));body.addView(c);}
    for(Move m:ms){
      LinearLayout c=card();c.setPadding(dp(12),dp(4),dp(12),dp(4));c.addView(moveRow(m));
      c.setOnLongClickListener(v->{new AlertDialog.Builder(this).setTitle("Eliminar movimiento").setMessage("¿Eliminar "+money.format(m.amount)+"?").setNegativeButton("Cancelar",null).setPositiveButton("Eliminar",(d,w)->{db.deleteMove(m.id);movements();}).show();return true;});
      body.addView(c);
    }
  }

  View moveRow(Move m){
    LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.setPadding(0,dp(10),0,dp(10));
    r.addView(iconBox(m.icon,TEXT,0x114F46E5,44),new LinearLayout.LayoutParams(dp(44),dp(44)));
    LinearLayout mid=new LinearLayout(this);mid.setOrientation(LinearLayout.VERTICAL);mid.setPadding(dp(10),0,dp(8),0);
    mid.addView(t(m.cat,15,TEXT,true));mid.addView(t(prettyDate(m.date)+(m.note.isEmpty()?"":" · "+m.note),12,MUTED,false));r.addView(mid,new LinearLayout.LayoutParams(0,-2,1));
    boolean in=m.type.equals("IN");TextView amt=t((in?"+ ":"- ")+money.format(m.amount),15,in?GREEN:RED,true);amt.setGravity(Gravity.END);r.addView(amt);return r;
  }

  void categories(){
    tab=2;nav();page("Categorías","Organiza tus movimientos a tu manera");
    MaterialButton add=btn("Crear categoría");add.setOnClickListener(v->categoryDialog());
    LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(54));p.setMargins(0,0,0,dp(16));body.addView(add,p);
    for(Category c:db.categories()){
      LinearLayout box=card();box.setOrientation(LinearLayout.HORIZONTAL);box.setGravity(Gravity.CENTER_VERTICAL);
      box.addView(iconBox(c.icon,TEXT,0x114F46E5,50),new LinearLayout.LayoutParams(dp(50),dp(50)));
      LinearLayout tx=new LinearLayout(this);tx.setOrientation(LinearLayout.VERTICAL);tx.setPadding(dp(11),0,0,0);tx.addView(t(c.name,16,TEXT,true));
      CT ct=db.categoryTotal(c.id);tx.addView(t("Entradas "+money.format(ct.in)+" · Salidas "+money.format(ct.out),12,MUTED,false));box.addView(tx,new LinearLayout.LayoutParams(0,-2,1));
      TextView hint=t("⋮",24,MUTED,true);hint.setGravity(Gravity.CENTER);box.addView(hint,new LinearLayout.LayoutParams(dp(30),dp(44)));
      box.setOnLongClickListener(v->{if(db.used(c.id)){Toast.makeText(this,"La categoría tiene movimientos",Toast.LENGTH_SHORT).show();}else new AlertDialog.Builder(this).setTitle("Eliminar categoría").setMessage("¿Eliminar “"+c.name+"”?").setNegativeButton("Cancelar",null).setPositiveButton("Eliminar",(d,w)->{db.deleteCategory(c.id);categories();}).show();return true;});
      body.addView(box);
    }
  }

  void categoryDialog(){
    final String[] keys={"food","car","home","health","shopping","work","package","school","party","flight","phone","receipt","money","category"};
    final String[] labels={"Comida","Auto","Hogar","Salud","Compras","Trabajo","Otros","Estudio","Ocio","Viaje","Teléfono","Recibo","Dinero","General"};
    final String[] selected={keys[0]};
    LinearLayout f=new LinearLayout(this);f.setOrientation(LinearLayout.VERTICAL);f.setPadding(dp(20),0,dp(20),0);
    EditText n=input("Nombre de categoría");f.addView(n,new LinearLayout.LayoutParams(-1,dp(56)));
    TextView choose=t("Elige un icono",13,MUTED,true);choose.setPadding(0,dp(16),0,dp(8));f.addView(choose);
    HorizontalScrollView hsv=new HorizontalScrollView(this);hsv.setHorizontalScrollBarEnabled(false);
    LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);final ArrayList<LinearLayout> cells=new ArrayList<>();
    for(int i=0;i<keys.length;i++){
      final int pos=i;LinearLayout cell=new LinearLayout(this);cell.setOrientation(LinearLayout.VERTICAL);cell.setGravity(Gravity.CENTER);cell.setPadding(dp(6),dp(7),dp(6),dp(5));
      cell.setBackground(outlined(i==0?0x194F46E5:0x99FFFFFF,16,i==0?0x664F46E5:0x44CBD5E1));
      cell.addView(iconBox(keys[i],i==0?ACCENT:TEXT,Color.TRANSPARENT,38),new LinearLayout.LayoutParams(dp(38),dp(36)));
      TextView lab=t(labels[i],10,MUTED,false);lab.setGravity(Gravity.CENTER);cell.addView(lab,new LinearLayout.LayoutParams(dp(70),dp(20)));
      cells.add(cell);
      cell.setOnClickListener(v->{selected[0]=keys[pos];for(int j=0;j<cells.size();j++){boolean on=j==pos;cells.get(j).setBackground(outlined(on?0x194F46E5:0x99FFFFFF,16,on?0x664F46E5:0x44CBD5E1));}});
      LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(dp(76),dp(66));cp.setMargins(0,0,dp(7),0);row.addView(cell,cp);
    }
    hsv.addView(row);f.addView(hsv,new LinearLayout.LayoutParams(-1,dp(72)));
    AlertDialog d=new AlertDialog.Builder(this).setTitle("Nueva categoría").setView(f).setNegativeButton("Cancelar",null).setPositiveButton("Crear",null).create();
    d.setOnShowListener(q->d.getButton(-1).setOnClickListener(v->{String name=n.getText().toString().trim();if(name.length()<2){n.setError("Escribe un nombre");return;}db.addCategory(name,selected[0]);d.dismiss();categories();}));d.show();
  }

  void movementDialog(){
    ArrayList<Category> cats=db.categories();if(cats.isEmpty()){Toast.makeText(this,"Primero crea una categoría",Toast.LENGTH_SHORT).show();categories();return;}
    LinearLayout f=new LinearLayout(this);f.setOrientation(LinearLayout.VERTICAL);f.setPadding(dp(20),0,dp(20),0);
    TextView typeLabel=t("Tipo de movimiento",13,MUTED,true);typeLabel.setPadding(0,0,0,dp(4));f.addView(typeLabel);
    RadioGroup rg=new RadioGroup(this);rg.setOrientation(RadioGroup.HORIZONTAL);
    MaterialRadioButton ri=new MaterialRadioButton(this);ri.setId(101);ri.setText("Entrada");ri.setTextColor(TEXT);
    MaterialRadioButton ro=new MaterialRadioButton(this);ro.setId(102);ro.setText("Salida");ro.setTextColor(TEXT);ro.setChecked(true);
    rg.addView(ri,new RadioGroup.LayoutParams(0,dp(48),1));rg.addView(ro,new RadioGroup.LayoutParams(0,dp(48),1));f.addView(rg);
    EditText amount=input("Monto, ej. 35.50");amount.setInputType(android.text.InputType.TYPE_CLASS_NUMBER|android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
    LinearLayout.LayoutParams fp=new LinearLayout.LayoutParams(-1,dp(56));fp.setMargins(0,dp(7),0,dp(9));f.addView(amount,fp);
    Spinner sp=new Spinner(this);sp.setBackground(outlined(0xBFFFFFFF,14,0x55CBD5E1));sp.setPadding(dp(12),0,dp(12),0);sp.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,cats));f.addView(sp,new LinearLayout.LayoutParams(-1,dp(56)));
    final String[] date={new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(new Date())};
    MaterialButton dateBtn=new MaterialButton(this);dateBtn.setAllCaps(false);dateBtn.setText("Fecha: "+prettyDate(date[0]));dateBtn.setTextColor(ACCENT);dateBtn.setCornerRadius(dp(14));dateBtn.setBackgroundTintList(ColorStateList.valueOf(0xBFFFFFFF));dateBtn.setStrokeColor(ColorStateList.valueOf(0x55CBD5E1));dateBtn.setStrokeWidth(dp(1));dateBtn.setInsetTop(0);dateBtn.setInsetBottom(0);
    dateBtn.setOnClickListener(v->{Calendar c=Calendar.getInstance();new DatePickerDialog(this,(vv,y,m,d)->{date[0]=String.format(Locale.US,"%04d-%02d-%02d",y,m+1,d);dateBtn.setText("Fecha: "+prettyDate(date[0]));},c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();});
    LinearLayout.LayoutParams dpv=new LinearLayout.LayoutParams(-1,dp(52));dpv.setMargins(0,dp(9),0,dp(9));f.addView(dateBtn,dpv);
    EditText note=input("Nota (opcional)");f.addView(note,new LinearLayout.LayoutParams(-1,dp(56)));
    AlertDialog d=new AlertDialog.Builder(this).setTitle("Nuevo movimiento").setView(f).setNegativeButton("Cancelar",null).setPositiveButton("Guardar",null).create();
    d.setOnShowListener(q->d.getButton(-1).setOnClickListener(v->{double a;try{a=Double.parseDouble(amount.getText().toString().trim().replace(",","."));}catch(Exception e){amount.setError("Monto inválido");return;}if(a<=0){amount.setError("Debe ser mayor a 0");return;}Category c=(Category)sp.getSelectedItem();db.addMove(rg.getCheckedRadioButtonId()==101?"IN":"OUT",a,c.id,note.getText().toString().trim(),date[0]);d.dismiss();if(tab==1)movements();else dashboard();}));d.show();
  }

  String prettyDate(String iso){try{return new SimpleDateFormat("dd MMM yyyy",new Locale("es","PE")).format(new SimpleDateFormat("yyyy-MM-dd",Locale.US).parse(iso));}catch(Exception e){return iso;}}

  static class Category{long id;String name,icon;Category(long i,String n,String c){id=i;name=n;icon=c;}public String toString(){return name;}}
  static class Move{long id;String type,note,date,cat,icon;double amount;}
  static class Totals{double in,out;}
  static class CT{long id;String name,icon;double in,out;}

  static class DB extends SQLiteOpenHelper{
    DB(Context c){super(c,"control_gastos.db",null,2);}
    public void onCreate(SQLiteDatabase d){
      d.execSQL("CREATE TABLE categories(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,icon TEXT NOT NULL)");
      d.execSQL("CREATE TABLE movements(id INTEGER PRIMARY KEY AUTOINCREMENT,type TEXT NOT NULL,amount REAL NOT NULL,category_id INTEGER NOT NULL,note TEXT NOT NULL DEFAULT '',date TEXT NOT NULL)");
      String[][] s={{"Alimentación","food"},{"Transporte","car"},{"Hogar","home"},{"Salud","health"},{"Compras","shopping"},{"Sueldo","work"},{"Otros","package"}};
      for(String[] x:s){ContentValues v=new ContentValues();v.put("name",x[0]);v.put("icon",x[1]);d.insert("categories",null,v);}
    }
    public void onUpgrade(SQLiteDatabase d,int o,int n){
      if(o<2){
        String[][] map={{"🍽️","food"},{"🚗","car"},{"🏠","home"},{"❤️","health"},{"🛍️","shopping"},{"💼","work"},{"📦","package"},{"🎓","school"},{"🎉","party"},{"✈️","flight"},{"📱","phone"},{"🧾","receipt"},{"💰","money"}};
        for(String[] x:map){ContentValues v=new ContentValues();v.put("icon",x[1]);d.update("categories",v,"icon=?",new String[]{x[0]});}
      }
    }
    void addCategory(String n,String i){ContentValues v=new ContentValues();v.put("name",n);v.put("icon",i);getWritableDatabase().insert("categories",null,v);}
    void deleteCategory(long id){getWritableDatabase().delete("categories","id=?",new String[]{""+id});}
    boolean used(long id){Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM movements WHERE category_id=?",new String[]{""+id});boolean r=c.moveToFirst()&&c.getLong(0)>0;c.close();return r;}
    ArrayList<Category> categories(){ArrayList<Category>a=new ArrayList<>();Cursor c=getReadableDatabase().rawQuery("SELECT id,name,icon FROM categories ORDER BY name",null);while(c.moveToNext())a.add(new Category(c.getLong(0),c.getString(1),c.getString(2)));c.close();return a;}
    void addMove(String type,double a,long id,String note,String date){ContentValues v=new ContentValues();v.put("type",type);v.put("amount",a);v.put("category_id",id);v.put("note",note);v.put("date",date);getWritableDatabase().insert("movements",null,v);}
    void deleteMove(long id){getWritableDatabase().delete("movements","id=?",new String[]{""+id});}
    Totals totals(){Totals t=new Totals();Cursor c=getReadableDatabase().rawQuery("SELECT COALESCE(SUM(CASE WHEN type='IN' THEN amount ELSE 0 END),0),COALESCE(SUM(CASE WHEN type='OUT' THEN amount ELSE 0 END),0) FROM movements",null);if(c.moveToFirst()){t.in=c.getDouble(0);t.out=c.getDouble(1);}c.close();return t;}
    ArrayList<Move> moves(int n){ArrayList<Move>a=new ArrayList<>();Cursor c=getReadableDatabase().rawQuery("SELECT m.id,m.type,m.amount,m.note,m.date,c.name,c.icon FROM movements m JOIN categories c ON c.id=m.category_id ORDER BY m.date DESC,m.id DESC LIMIT "+Math.max(1,n),null);while(c.moveToNext()){Move m=new Move();m.id=c.getLong(0);m.type=c.getString(1);m.amount=c.getDouble(2);m.note=c.getString(3);m.date=c.getString(4);m.cat=c.getString(5);m.icon=c.getString(6);a.add(m);}c.close();return a;}
    ArrayList<CT> categoryTotals(){ArrayList<CT>a=new ArrayList<>();Cursor c=getReadableDatabase().rawQuery("SELECT c.id,c.name,c.icon,COALESCE(SUM(CASE WHEN m.type='IN' THEN m.amount ELSE 0 END),0),COALESCE(SUM(CASE WHEN m.type='OUT' THEN m.amount ELSE 0 END),0) FROM categories c LEFT JOIN movements m ON m.category_id=c.id GROUP BY c.id,c.name,c.icon ORDER BY c.name",null);while(c.moveToNext()){CT x=new CT();x.id=c.getLong(0);x.name=c.getString(1);x.icon=c.getString(2);x.in=c.getDouble(3);x.out=c.getDouble(4);a.add(x);}c.close();return a;}
    CT categoryTotal(long id){CT x=new CT();Cursor c=getReadableDatabase().rawQuery("SELECT COALESCE(SUM(CASE WHEN type='IN' THEN amount ELSE 0 END),0),COALESCE(SUM(CASE WHEN type='OUT' THEN amount ELSE 0 END),0) FROM movements WHERE category_id=?",new String[]{""+id});if(c.moveToFirst()){x.in=c.getDouble(0);x.out=c.getDouble(1);}c.close();return x;}
  }

  static class IconView extends View{
    Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);String key;int color;
    IconView(Context c,String k,int col){super(c);key=k==null?"category":k;color=col;p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2.2f*c.getResources().getDisplayMetrics().density);p.setStrokeCap(Paint.Cap.ROUND);p.setStrokeJoin(Paint.Join.ROUND);}
    protected void onDraw(Canvas c){super.onDraw(c);p.setColor(color);p.setStyle(Paint.Style.STROKE);float w=getWidth(),h=getHeight(),s=Math.min(w,h),x=(w-s)/2,y=(h-s)/2;float a=x+s*.18f,b=y+s*.18f,d=x+s*.82f,e=y+s*.82f,mx=x+s*.5f,my=y+s*.5f;Path q=new Path();
      switch(key){
        case "home": q.moveTo(a,my);q.lineTo(mx,b);q.lineTo(d,my);q.lineTo(d,e);q.lineTo(mx+s*.12f,e);q.lineTo(mx+s*.12f,my+s*.12f);q.lineTo(mx-s*.12f,my+s*.12f);q.lineTo(mx-s*.12f,e);q.lineTo(a,e);q.close();c.drawPath(q,p);break;
        case "swap": c.drawLine(mx-s*.15f,b,mx-s*.15f,e,p);c.drawLine(mx-s*.15f,b,mx-s*.28f,b+s*.14f,p);c.drawLine(mx-s*.15f,b,mx-s*.02f,b+s*.14f,p);c.drawLine(mx+s*.15f,e,mx+s*.15f,b,p);c.drawLine(mx+s*.15f,e,mx+s*.02f,e-s*.14f,p);c.drawLine(mx+s*.15f,e,mx+s*.28f,e-s*.14f,p);break;
        case "category": for(int rr=0;rr<2;rr++)for(int cc=0;cc<2;cc++){float l=x+s*(.18f+cc*.36f),tt=y+s*(.18f+rr*.36f);c.drawRoundRect(l,tt,l+s*.22f,tt+s*.22f,s*.04f,s*.04f,p);}break;
        case "wallet": c.drawRoundRect(a,y+s*.27f,d,y+s*.73f,s*.08f,s*.08f,p);c.drawRoundRect(x+s*.54f,y+s*.40f,d+s*.04f,y+s*.60f,s*.05f,s*.05f,p);c.drawCircle(x+s*.65f,my,s*.025f,p);break;
        case "up": c.drawLine(mx,e,mx,b,p);c.drawLine(mx,b,mx-s*.16f,b+s*.16f,p);c.drawLine(mx,b,mx+s*.16f,b+s*.16f,p);break;
        case "down": c.drawLine(mx,b,mx,e,p);c.drawLine(mx,e,mx-s*.16f,e-s*.16f,p);c.drawLine(mx,e,mx+s*.16f,e-s*.16f,p);break;
        case "food": c.drawLine(x+s*.32f,b,x+s*.32f,e,p);c.drawLine(x+s*.23f,b,x+s*.23f,y+s*.42f,p);c.drawLine(x+s*.32f,b,x+s*.32f,y+s*.42f,p);c.drawLine(x+s*.41f,b,x+s*.41f,y+s*.42f,p);c.drawLine(x+s*.23f,y+s*.42f,x+s*.41f,y+s*.42f,p);q.moveTo(x+s*.66f,b);q.lineTo(x+s*.66f,e);q.moveTo(x+s*.66f,b);q.quadTo(x+s*.80f,y+s*.35f,x+s*.66f,y+s*.47f);c.drawPath(q,p);break;
        case "car": q.moveTo(a,y+s*.60f);q.lineTo(a+s*.08f,y+s*.42f);q.quadTo(mx,y+s*.32f,d-s*.08f,y+s*.42f);q.lineTo(d,y+s*.60f);q.lineTo(d,y+s*.72f);q.lineTo(a,y+s*.72f);q.close();c.drawPath(q,p);c.drawCircle(x+s*.32f,y+s*.72f,s*.07f,p);c.drawCircle(x+s*.68f,y+s*.72f,s*.07f,p);break;
        case "health": p.setStyle(Paint.Style.FILL);c.drawRect(mx-s*.08f,b,mx+s*.08f,e,p);c.drawRect(a,my-s*.08f,d,my+s*.08f,p);break;
        case "shopping": c.drawRoundRect(x+s*.25f,y+s*.34f,x+s*.75f,e,s*.05f,s*.05f,p);q.moveTo(x+s*.38f,y+s*.38f);q.quadTo(mx,y+s*.14f,x+s*.62f,y+s*.38f);c.drawPath(q,p);break;
        case "work": c.drawRoundRect(a,y+s*.34f,d,e,s*.06f,s*.06f,p);c.drawRect(x+s*.40f,y+s*.24f,x+s*.60f,y+s*.36f,p);c.drawLine(a,my,d,my,p);break;
        case "package": c.drawRect(x+s*.22f,y+s*.28f,x+s*.78f,y+s*.76f,p);c.drawLine(x+s*.22f,y+s*.28f,mx,y+s*.47f,p);c.drawLine(x+s*.78f,y+s*.28f,mx,y+s*.47f,p);c.drawLine(mx,y+s*.47f,mx,y+s*.76f,p);break;
        case "school": q.moveTo(a,y+s*.42f);q.lineTo(mx,y+s*.24f);q.lineTo(d,y+s*.42f);q.lineTo(mx,y+s*.60f);q.close();c.drawPath(q,p);c.drawLine(x+s*.32f,y+s*.53f,x+s*.32f,y+s*.70f,p);c.drawLine(x+s*.68f,y+s*.53f,x+s*.68f,y+s*.70f,p);break;
        case "party": c.drawLine(x+s*.34f,e,x+s*.58f,y+s*.38f,p);q.moveTo(x+s*.30f,e);q.lineTo(x+s*.58f,y+s*.38f);q.lineTo(x+s*.68f,y+s*.68f);q.close();c.drawPath(q,p);c.drawCircle(x+s*.72f,y+s*.28f,s*.03f,p);c.drawLine(x+s*.52f,y+s*.20f,x+s*.58f,y+s*.28f,p);c.drawLine(x+s*.78f,y+s*.44f,x+s*.86f,y+s*.40f,p);break;
        case "flight": q.moveTo(a,my);q.lineTo(d,b);q.lineTo(x+s*.61f,e);q.lineTo(mx,y+s*.57f);q.lineTo(a,my);q.close();c.drawPath(q,p);break;
        case "phone": c.drawRoundRect(x+s*.32f,b,x+s*.68f,e,s*.06f,s*.06f,p);c.drawLine(x+s*.44f,y+s*.26f,x+s*.56f,y+s*.26f,p);c.drawCircle(mx,y+s*.72f,s*.02f,p);break;
        case "receipt": q.moveTo(x+s*.30f,b);q.lineTo(x+s*.70f,b);q.lineTo(x+s*.70f,e);q.lineTo(x+s*.62f,e-s*.07f);q.lineTo(x+s*.54f,e);q.lineTo(x+s*.46f,e-s*.07f);q.lineTo(x+s*.38f,e);q.lineTo(x+s*.30f,e-s*.07f);q.close();c.drawPath(q,p);c.drawLine(x+s*.39f,y+s*.38f,x+s*.61f,y+s*.38f,p);c.drawLine(x+s*.39f,my,x+s*.61f,my,p);break;
        case "money": c.drawCircle(mx,my,s*.28f,p);p.setStyle(Paint.Style.FILL);p.setTextAlign(Paint.Align.CENTER);p.setTextSize(s*.34f);p.setTypeface(Typeface.DEFAULT_BOLD);c.drawText("S",mx,my+s*.12f,p);break;
        default: for(int rr=0;rr<2;rr++)for(int cc=0;cc<2;cc++){float l=x+s*(.18f+cc*.36f),tt=y+s*(.18f+rr*.36f);c.drawRoundRect(l,tt,l+s*.22f,tt+s*.22f,s*.04f,s*.04f,p);}break;
      }
    }
  }

  static class Bars extends View{
    Paint p=new Paint(1);double a,b;NumberFormat nf=NumberFormat.getCurrencyInstance(new Locale("es","PE"));
    Bars(Context c,double i,double o){super(c);a=i;b=o;}
    protected void onDraw(Canvas c){float w=getWidth(),h=getHeight(),base=h-36;double mx=Math.max(1,Math.max(a,b));double[] vals={a,b};int[] cols={GREEN,RED};String[] names={"Entradas","Salidas"};for(int i=0;i<2;i++){float x=w*(i==0?.30f:.70f),bw=Math.min(78,w*.19f),bh=(float)((base-44)*(vals[i]/mx));p.setColor(0x18CBD5E1);c.drawRoundRect(x-bw/2,32,x+bw/2,base,18,18,p);p.setColor(cols[i]);c.drawRoundRect(x-bw/2,base-bh,x+bw/2,base,18,18,p);p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextSize(25);p.setColor(TEXT);c.drawText(nf.format(vals[i]),x,22,p);p.setTextSize(23);p.setTypeface(Typeface.DEFAULT);p.setColor(MUTED);c.drawText(names[i],x,h-7,p);}}
  }

  static class CatBars extends View{
    Paint p=new Paint(1);ArrayList<CT>d;NumberFormat nf=NumberFormat.getCurrencyInstance(new Locale("es","PE"));
    CatBars(Context c,ArrayList<CT>x){super(c);d=x;}
    protected void onDraw(Canvas c){float w=getWidth(),y=25;double mx=1;for(CT x:d)mx=Math.max(mx,Math.max(x.in,x.out));for(CT x:d){p.setTextAlign(Paint.Align.LEFT);p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextSize(25);p.setColor(TEXT);c.drawText(x.name,4,y,p);float left=Math.min(122,w*.36f),right=w-6,bw=right-left;y+=13;p.setColor(0x1A059669);c.drawRoundRect(left,y,right,y+10,5,5,p);p.setColor(GREEN);c.drawRoundRect(left,y,left+(float)(bw*x.in/mx),y+10,5,5,p);p.setTypeface(Typeface.DEFAULT);p.setTextSize(18);p.setColor(MUTED);c.drawText("E "+nf.format(x.in),4,y+10,p);y+=23;p.setColor(0x1ADC2626);c.drawRoundRect(left,y,right,y+10,5,5,p);p.setColor(RED);c.drawRoundRect(left,y,left+(float)(bw*x.out/mx),y+10,5,5,p);p.setColor(MUTED);c.drawText("S "+nf.format(x.out),4,y+10,p);y+=36;}}
  }
}
