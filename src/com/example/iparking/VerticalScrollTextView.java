package com.example.iparking;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * TODO 
 * @author cuiran
 * @version 1.0.0
 */
public class VerticalScrollTextView extends TextView {

		private float step =0f;   
	    private Paint mPaint=new Paint(); ; 
	    private String text; 
	    private float width; 
	    private List<String> textList = new ArrayList<String>();    //���б���textview����ʾ��Ϣ�� 
	 
	    public VerticalScrollTextView(Context context, AttributeSet attrs) { 
	        super(context, attrs);         
	    } 
	     
	 
	    public VerticalScrollTextView(Context context) { 
	        super(context);         
	    } 
	     
	    @Override 
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {         
	        super.onMeasure(widthMeasureSpec, heightMeasureSpec); 
	        width = MeasureSpec.getSize(widthMeasureSpec);    
	        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);   
	        if (widthMode != MeasureSpec.EXACTLY) {    
	            throw new IllegalStateException("ScrollLayout only canmCurScreen run at EXACTLY mode!"); 
	        }       
	       
	        text=getText().toString();
	        if(text==null|text.length()==0){ 
	               
	        	return ; 
	        }     
	   
	        
	       //����Ĵ����Ǹ��ݿ�Ⱥ������С��������textview��ʾ�������� 
	 
	        textList.clear(); 
	        StringBuilder builder =null;
	        int nLineWidth = 19;  //ÿ����ʾ19����
	        for(int i=0;i<text.length();i++){
	        	if(i%nLineWidth==0){
	        		builder = new StringBuilder(); 
	        	}
	        	 if(i%nLineWidth<=nLineWidth-1){ 
	        		 builder.append(text.charAt(i)); 
	        	 }
	        	 if(i%nLineWidth==nLineWidth-1){ 
	        		 textList.add(builder.toString()); 
	        	 }
	        	 
	        }
	        Log.e("textviewscroll",""+textList.size()); 
	       

	    } 
	 
	 
	    //�����������������������ʾ�����������ֻ��ڻ����ϣ�ʵʱ���¡� 
	     @Override 
	    public void onDraw(Canvas canvas) { 
	       if(textList.size()==0)  return; 
	       
	       mPaint.setTextSize(75);//���������С
	       mPaint.setColor(Color.WHITE); //��ɫ��
	        for(int i = 0; i < textList.size(); i++) { 
	            canvas.drawText(textList.get(i), 0, this.getHeight()+(i+1)*mPaint.getTextSize()-step+30, mPaint); 
	        } 
	        invalidate();    
	        
	        step = step+0.8f; //���ƹ����ٶ�
	        if (step >= this.getHeight()+textList.size()*mPaint.getTextSize()) { 
	            step = 0; 
	        }         
	    } 
	 
}
