//
//  SecondViewController.h
//  Ilmoitus
//
//  Created by Alexander Bolhuis on 22-04-14.
//  Copyright (c) 2014 42IN12EWa. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Declaration.h"

@interface NewDeclarationViewController : UIViewController <UITextFieldDelegate, UITextViewDelegate, UITableViewDataSource, UITableViewDelegate>
@property Declaration *declaration;

@end
